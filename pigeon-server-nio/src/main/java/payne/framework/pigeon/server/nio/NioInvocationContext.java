package payne.framework.pigeon.server.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import payne.framework.pigeon.server.InvocationContext;
import payne.framework.pigeon.server.Status;
import payne.framework.pigeon.server.exception.ContextRunningException;

public class NioInvocationContext extends HTTPInvocationContext implements InvocationContext {
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private int byteBufferSize;
	private Queue<Registration> registrations = new ConcurrentLinkedQueue<Registration>();

	public NioInvocationContext() throws IOException {
		this(1024);
	}

	public NioInvocationContext(int byteBufferSize) throws IOException {
		this.byteBufferSize = byteBufferSize;
	}

	public NioInvocationContext(Object... openables) {
		super(openables);
	}

	public void run() {
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(address);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Throwable e) {
			throw new ContextRunningException(e);
		}
		ByteBuffer buffer = ByteBuffer.allocate(byteBufferSize);
		status = Status.STARTUP;
		while (status != Status.SHUTDOWN) {
			try {
				synchronized (registrations) {
					while (!registrations.isEmpty()) {
						Registration registration = registrations.poll();
						try {
							registration.channel.register(selector, registration.options, registration.attachment);
						} catch (Exception e) {
							SelectionKey key = registration.channel.keyFor(selector);
							IOToolkit.close(key);
						}
					}
				}
				if (selector.select() == 0) {
					continue;
				}
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey key = null;
					try {
						key = iterator.next();
						iterator.remove();
						if (key.isValid() && key.isAcceptable()) {
							SocketChannel channel = serverSocketChannel.accept();
							logger.debug("accepted socket client {}", channel.socket().getRemoteSocketAddress());
							channel.configureBlocking(false);
							channel.register(selector, SelectionKey.OP_READ, new Request());
						}
						if (key.isValid() && key.isReadable()) {
							buffer.clear();
							Request request = (Request) key.attachment();
							SocketChannel channel = (SocketChannel) key.channel();
							channel.read(buffer);
							buffer.flip();
							if (request.receive(buffer)) {
								NonBlockingHTTPInvocationHandler handler = new NonBlockingHTTPInvocationHandler(key, request);
								synchronized (handler) {
									Future<?> future = executor.submit(handler);
									handler.bind(future);
								}
							}
						}
						if (key.isValid() && key.isWritable()) {
							Response response = (Response) key.attachment();
							SocketChannel channel = (SocketChannel) key.channel();
							if (response.send(channel, buffer.capacity())) {
								logger.debug("client {} handle completed", channel.socket().getRemoteSocketAddress());
								IOToolkit.close(response);
								IOToolkit.close(key);
							}
						}
					} catch (Throwable e) {
						IOToolkit.close(key);
						logger.error("", e);
					}
				}
			} catch (Throwable e) {
				// 客户端连接异常 忽略
				logger.error("", e);
			}
		}
	}

	// 正常退出
	public void shutdown() {
		if (status == Status.SHUTDOWN) {
			return;
		}
		status = Status.SHUTDOWN;
		IOToolkit.close(serverSocketChannel);
		IOToolkit.close(selector);
		executor.shutdown();
		logger.warn("invocation context of address {} shutdown", address);
		notificationCenter.notify(new Event(CONTEXT_SHUTDOWN_EVENT_NAME, this, null));
	}

	public int getByteBufferSize() {
		return byteBufferSize;
	}

	public void setByteBufferSize(int byteBufferSize) {
		this.byteBufferSize = byteBufferSize;
	}

	private class NonBlockingHTTPInvocationHandler extends HTTPInvocationHandler implements Constants {
		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		private final SelectionKey key;
		private final Request request;
		private Channel channel;

		public NonBlockingHTTPInvocationHandler(SelectionKey key, Request request) {
			super();
			this.key = key;
			this.request = request;
		}

		@Override
		protected void handle() {
			SocketChannel sc = null;
			InputStream in = null;
			OutputStream out = null;
			Head head = null;
			try {
				sc = (SocketChannel) key.channel();

				in = streamFactory.produce(request.data);
				out = streamFactory.produce();

				head = new Head(IOToolkit.readLine(in));

				if (!beanFactory.contains(head.getProtocol())) {
					throw new UnsupportedChannelException(head.getProtocol());
				}

				channel = beanFactory.establish(head.getProtocol(), Channel.class);
				channel.initialize(head.getProtocol(), head.getMode(), head.getFile(), head.getParameter(), sc.socket().getRemoteSocketAddress(), in, out);
				channel.setCharset(charset);
				channel.getAttributes().putAll(attributes);

				notificationCenter.notify(new Event(CONNECTION_ACCEPT_EVENT_NAME, channel, null));

				if (!exists(head.getMode(), head.getFile())) {
					throw new UnmappedPathException(head.getFile());
				}

				Set<Filter<Channel>> _filters = new LinkedHashSet<Filter<Channel>>(filters);
				_filters.add(NioInvocationContext.this);
				new FixedFilterChain<Channel>(_filters).go(channel);
			} catch (Throwable e) {
				logger.error("handling client {} but occur exception", sc.socket(), e);
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
					synchronized (registrations) {
						registrations.offer(new Registration(sc, SelectionKey.OP_WRITE, new Response(streamFactory.produce(out))));
					}
					selector.wakeup();
				} catch (Throwable e) {
					logger.error("response client {} but occur exception", sc.socket(), e);
				} finally {
					IOToolkit.close(this);
					notificationCenter.notify(new Event(CONNECTION_CLOSE_EVENT_NAME, channel, null));
				}
			}
		}

		public void close() throws IOException {
			IOToolkit.close(request);
			IOToolkit.close(channel);
		}

	}

	private class Registration {
		private final SelectableChannel channel;
		private final int options;
		private final Object attachment;

		private Registration(SelectableChannel channel, int options, Object attachment) {
			super();
			this.channel = channel;
			this.options = options;
			this.attachment = attachment;
		}

	}

	private class Request implements Constants, Closeable {
		private final ByteArrayOutputStream head;
		private final OutputStream data;

		private Boolean chunked;
		private int total;
		private int position;

		private byte[] last;
		private int line;

		private Request() throws IOException {
			super();
			this.head = new ByteArrayOutputStream();
			this.data = streamFactory.produce();
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
							buffer = ByteBuffer.wrap(bytes, i + 1, bytes.length - i - 1);
							return receive(buffer);
						}
					} else if (b != '\r') {
						line = 0;
					}
				}
				return false;
			} else if (chunked) {
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
				return receive(ByteBuffer.wrap(bytes, 0, length));
			} else {
				data.write(bytes);
				position += bytes.length;
				return position >= total;
			}
		}

		public void close() throws IOException {
			IOToolkit.close(head);
			IOToolkit.close(data);
		}

	}

	private class Response implements Closeable {
		private final InputStream result;
		private int position = 0;

		private Response(InputStream result) {
			super();
			this.result = result;
		}

		private boolean send(SocketChannel channel, int size) throws IOException {
			result.reset();
			result.skip(position);
			byte[] buffer = new byte[size];
			int length = result.read(buffer);
			if (length != -1) {
				length = channel.write(ByteBuffer.wrap(buffer, 0, length));
				position += length;
			}
			return result.available() == 0;
		}

		public void close() throws IOException {
			IOToolkit.close(result);
		}

	}

}
