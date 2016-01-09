package payne.framework.pigeon.server.aio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.LinkedHashSet;
import java.util.Set;

import payne.framework.pigeon.core.Constants;
import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.exception.UnmappedPathException;
import payne.framework.pigeon.core.exception.UnsupportedChannelException;
import payne.framework.pigeon.core.filtration.Filter;
import payne.framework.pigeon.core.filtration.FixedFilterChain;
import payne.framework.pigeon.core.observation.Event;
import payne.framework.pigeon.core.protocol.Channel;
import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.server.HTTPInvocationContext;
import payne.framework.pigeon.server.Head;
import payne.framework.pigeon.server.Status;
import payne.framework.pigeon.server.exception.ContextRunningException;

public class AioInvocationContext extends HTTPInvocationContext {
	private AsynchronousChannelGroup asynchronousChannelGroup;
	private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

	private final ByteBuffer emptyByteBuffer = ByteBuffer.allocate(0);

	private int byteBufferSize = 1024;

	public AioInvocationContext() throws IOException {
		this(1024);
	}

	public AioInvocationContext(int byteBufferSize) throws IOException {
		this.byteBufferSize = byteBufferSize;
	}

	public AioInvocationContext(Object... openables) {
		super(openables);
	}

	public void run() {
		try {
			asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
			asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open(asynchronousChannelGroup);
			asynchronousServerSocketChannel.bind(address);
			asynchronousServerSocketChannel.accept(null, new AcceptCompletionHandler());
			status = Status.STARTUP;
		} catch (Throwable e) {
			throw new ContextRunningException(e);
		}
	}

	public void shutdown() {
		if (status == Status.SHUTDOWN) {
			return;
		}
		status = Status.SHUTDOWN;
		close(asynchronousServerSocketChannel);
		close(asynchronousChannelGroup);
		executor.shutdown();
		logger.warn("invocation context of address {} shutdown", address);
		notificationCenter.notify(new Event(CONTEXT_SHUTDOWN_EVENT_NAME, this, null));
	}

	private void close(AsynchronousChannelGroup acg) {
		if (acg == null || acg.isShutdown()) {
			return;
		}
		try {
			asynchronousChannelGroup.shutdown();
		} catch (Exception e) {
			logger.error("error while shutting down {}", acg, e);
		}
	}

	private void close(AsynchronousServerSocketChannel assc) {
		if (assc == null || !assc.isOpen()) {
			return;
		}
		try {
			asynchronousServerSocketChannel.close();
		} catch (Exception e) {
			logger.error("error while closing {}", assc, e);
		}
	}

	private void close(AsynchronousSocketChannel asc) {
		if (asc == null || !asc.isOpen()) {
			return;
		}
		try {
			asc.close();
		} catch (Throwable e) {
			logger.error("error while closing {}", asc, e);
		}
	}

	private class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

		public void completed(AsynchronousSocketChannel connection, Object attachment) {
			try {
				logger.debug("accepted socket client {}", connection);
				ByteBuffer buffer = ByteBuffer.allocate(byteBufferSize);
				connection.read(buffer, buffer, new ReadCompletionHandler(connection));
			} catch (Throwable e) {
				logger.error("register read completion handler fail", e);
			} finally {
				asynchronousServerSocketChannel.accept(attachment, this);
			}
		}

		public void failed(Throwable exception, Object attachment) {
			logger.error("error while accepting connection with attachment {}", attachment, exception);
		}

	}

	private class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer>, Constants {
		private final AsynchronousSocketChannel asynchronousSocketChannel;
		private final ByteArrayOutputStream head;
		private final OutputStream data;

		private Boolean chunked;
		private int total;
		private int position;

		private byte[] last;
		private int line;

		private ReadCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel) throws IOException {
			super();
			this.asynchronousSocketChannel = asynchronousSocketChannel;
			this.head = new ByteArrayOutputStream();
			this.data = streamFactory.produce();
		}

		public void completed(Integer count, ByteBuffer buffer) {
			if (count < 0) {
				AsynchronousNonBlockingHTTPInvocationHandler handler = null;
				try {
					handler = new AsynchronousNonBlockingHTTPInvocationHandler(asynchronousSocketChannel, streamFactory.produce(data));
					handler.run();
				} catch (Throwable e) {
					logger.error("error while reading connection {}", asynchronousSocketChannel, e);
					close(asynchronousSocketChannel);
				} finally {
					logger.debug("client {} handle completed", asynchronousSocketChannel);
					IOToolkit.close(handler);
				}
				return;
			}
			try {
				buffer.flip();
				if (receive(buffer)) {
					completed(-1, buffer);
					return;
				} else {
					buffer.clear();
					asynchronousSocketChannel.read(buffer, buffer, this);
				}
			} catch (Throwable e) {
				logger.error("error while reading connection {}", asynchronousSocketChannel, e);
				close(asynchronousSocketChannel);
			}
		}

		public void failed(Throwable exception, ByteBuffer buffer) {
			close(asynchronousSocketChannel);
			logger.error("error while reading data from connection {}", asynchronousSocketChannel, exception);
		}

		private boolean receive(ByteBuffer buffer) throws IOException {
			if (buffer.remaining() == 0) {
				return false;
			}
			// 如果还有上次的 则放在前面
			byte[] bytes = new byte[buffer.remaining() + (last == null ? 0 : last.length)];
			if (last != null) {
				System.arraycopy(last, 0, bytes, 0, last.length);
			}
			buffer.get(bytes, last == null ? 0 : last.length, buffer.remaining());
			last = null;
			if (chunked == null) {
				return doHeaderReceive(bytes);
			} else if (chunked) {
				return doChunkedReceive(bytes);
			} else {
				return doLengthReceive(bytes);
			}
		}

		private boolean doHeaderReceive(byte[] bytes) throws IOException {
			for (int i = 0; i < bytes.length; i++) {
				byte b = bytes[i];
				head.write(b);
				data.write(b);
				if (b == '\n') {
					line++;
					// header 读完
					if (line == 2) {
						ByteArrayInputStream in = new ByteArrayInputStream(head.toByteArray());
						Head head = new Head(IOToolkit.readLine(in));
						if (head.getMode().bodied == false) {
							return true;
						}
						Header header = new Header();
						String line = null;
						while ((line = IOToolkit.readLine(in)) != null) {
							int index = line.indexOf(':');
							if (index == -1) {
								continue;
							}
							header.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
						}
						String te = header.getTransferEncoding();
						if (te != null && te.trim().toLowerCase().equals("chunked")) {
							chunked = true;
							total = 0;
							position = 0;
							last = new byte[] { '\r', '\n' };
						} else {
							chunked = false;
							total = header.getContentLength();
							position = 0;
						}
						ByteBuffer buffer = ByteBuffer.wrap(bytes, i + 1, bytes.length - i - 1);
						return receive(buffer);
					}
				} else if (b != '\r') {
					line = 0;
				}
			}
			return false;
		}

		private boolean doChunkedReceive(byte[] bytes) throws IOException {
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			int length;
			if (position >= total) {
				IOToolkit.readLine(in);

				// 如果没有包含换行符 则保存起来
				int count = 0;
				for (int i = 0; i < bytes.length; i++) {
					if (bytes[i] == '\n') {
						count++;
						if (count == 2) {
							break;
						}
					}
				}
				if (count < 2) {
					last = bytes;
					return false;
				}

				total = Integer.valueOf(IOToolkit.readLine(in), 16);
				position = 0;
				if (total == 0) {
					IOToolkit.writeLine(Integer.toHexString(0), data);
					IOToolkit.writeLine("", data);
					return true;
				}
				length = Math.min(total, in.available());
				IOToolkit.writeLine(Integer.toHexString(total), data);
			} else {
				length = Math.min(total - position, in.available());
			}
			IOToolkit.transmit(in, length, data);
			position += length;
			if (position == total) {
				IOToolkit.writeLine("", data);
			}
			length = in.available();
			in.read(bytes, 0, length);
			ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, length);
			return receive(buffer);
		}

		private boolean doLengthReceive(byte[] bytes) throws IOException {
			data.write(bytes);
			position += bytes.length;
			return position >= total;
		}

	}

	private class WriteCompletionHandler implements CompletionHandler<Integer, InputStream> {
		private final AsynchronousSocketChannel asynchronousSocketChannel;
		private final ByteBuffer buffer;
		private final byte[] bytes;
		private int length;

		private WriteCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel) {
			super();
			this.asynchronousSocketChannel = asynchronousSocketChannel;
			this.buffer = ByteBuffer.allocate(byteBufferSize);
			this.bytes = new byte[byteBufferSize];
		}

		public void completed(Integer result, InputStream response) {
			try {
				if (response.available() <= 0) {
					IOToolkit.close(response);
					close(asynchronousSocketChannel);
				} else {
					length = response.read(bytes);
					buffer.clear();
					buffer.put(bytes, 0, length);
					buffer.flip();
					asynchronousSocketChannel.write(buffer, response, this);
				}
			} catch (Throwable e) {
				logger.error("error while writting data to connection {}", asynchronousSocketChannel, e);
				IOToolkit.close(response);
				close(asynchronousSocketChannel);
			}
		}

		public void failed(Throwable exception, InputStream response) {
			IOToolkit.close(response);
			close(asynchronousSocketChannel);
			logger.error("error while writting data to connection {}", asynchronousSocketChannel, exception);
		}

	}

	private class AsynchronousNonBlockingHTTPInvocationHandler extends HTTPInvocationHandler implements Constants {
		private final AsynchronousSocketChannel asynchronousSocketChannel;
		private final InputStream request;
		private Channel channel;

		private AsynchronousNonBlockingHTTPInvocationHandler(AsynchronousSocketChannel asynchronousSocketChannel, InputStream request) {
			super();
			this.asynchronousSocketChannel = asynchronousSocketChannel;
			this.request = request;
		}

		@Override
		protected void handle() {
			InputStream in = null;
			OutputStream out = null;
			Head head = null;
			try {
				in = request;
				out = streamFactory.produce();

				head = new Head(IOToolkit.readLine(in));

				if (!beanFactory.contains(head.getProtocol())) {
					throw new UnsupportedChannelException(head.getProtocol());
				}

				channel = beanFactory.establish(head.getProtocol(), Channel.class);
				channel.initialize(head.getProtocol(), head.getMode(), head.getFile(), head.getParameter(), asynchronousSocketChannel.getRemoteAddress(), in, out);
				channel.setCharset(charset);
				channel.getAttributes().putAll(attributes);

				notificationCenter.notify(new Event(CONNECTION_ACCEPT_EVENT_NAME, channel, null));

				if (!exists(head.getMode(), head.getFile())) {
					throw new UnmappedPathException(head.getFile());
				}

				Set<Filter<Channel>> _filters = new LinkedHashSet<Filter<Channel>>(filters);
				_filters.add(AioInvocationContext.this);
				new FixedFilterChain<Channel>(_filters).go(channel);
			} catch (Throwable e) {
				logger.error("handling client {} but occur exception", asynchronousSocketChannel, e);
				if (channel != null && channel.getWrited() > 0) {
					return;
				}
				try {
					fail(e, head, out);
				} catch (IOException ioe) {
					logger.error("", ioe);
				} finally {
					notificationCenter.notify(new Event(CONNECTION_ERROR_EVENT_NAME, channel, e));
				}
			} finally {
				try {
					asynchronousSocketChannel.write(emptyByteBuffer, streamFactory.produce(out), new WriteCompletionHandler(asynchronousSocketChannel));
				} catch (Throwable e) {
					logger.error("response client {} but occur exception", asynchronousSocketChannel, e);
				} finally {
					IOToolkit.close(in);
					IOToolkit.close(out);
					notificationCenter.notify(new Event(CONNECTION_CLOSE_EVENT_NAME, channel, null));
				}
			}
		}

		public void close() throws IOException {
			IOToolkit.close(channel);
			IOToolkit.close(request);
		}

	}

	public int getByteBufferSize() {
		return byteBufferSize;
	}

	public void setByteBufferSize(int byteBufferSize) {
		this.byteBufferSize = byteBufferSize;
	}

}
