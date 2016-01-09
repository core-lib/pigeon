package payne.framework.pigeon.server.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import payne.framework.pigeon.core.Constants;
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

public class BioInvocationContext extends HTTPInvocationContext implements InvocationContext {
	private ServerSocket server = null;

	public BioInvocationContext(Object... openables) {
		super(openables);
	}

	public void run() {
		try {
			server = new ServerSocket();
			server.bind(address);
		} catch (Exception e) {
			throw new ContextRunningException(e);
		}
		status = Status.STARTUP;
		Socket client = null;
		while (status != Status.SHUTDOWN) {
			try {
				try {
					client = server.accept();
					logger.debug("accepted socket client {}", client);
				} catch (SocketException e) {
					if (server.isClosed()) {
						break;
					}
					logger.error("accepting client exception", e);
					continue;
				}
				BlockingHTTPInvocationHandler handler = new BlockingHTTPInvocationHandler(client);
				synchronized (handler) {
					Future<?> future = executor.submit(handler);
					handler.bind(future);
				}
			} catch (Throwable e) {
				logger.error("handling socket client {} throws exception", client, e);
				IOToolkit.close(client);
			}
		}
	}

	// 正常退出
	public void shutdown() {
		if (status == Status.SHUTDOWN) {
			return;
		}
		status = Status.SHUTDOWN;
		IOToolkit.close(server);
		executor.shutdown();
		logger.warn("invocation context of address {} shutdown", address);
		notificationCenter.notify(new Event(CONTEXT_SHUTDOWN_EVENT_NAME, this, null));
	}

	private class BlockingHTTPInvocationHandler extends HTTPInvocationHandler implements Constants {
		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		private final Socket client;
		private final InputStream inputStream;
		private final OutputStream outputStream;

		public BlockingHTTPInvocationHandler(Socket client) throws IOException {
			super();
			this.client = client;
			this.inputStream = client.getInputStream();
			this.outputStream = client.getOutputStream();
		}

		protected void handle() {
			Channel channel = null;
			Head head = null;
			try {
				head = new Head(IOToolkit.readLine(inputStream));

				if (!beanFactory.contains(head.getProtocol())) {
					throw new UnsupportedChannelException(head.getProtocol());
				}

				channel = beanFactory.establish(head.getProtocol(), Channel.class);
				channel.initialize(head.getProtocol(), head.getMode(), head.getFile(), head.getParameter(), client.getRemoteSocketAddress(), inputStream, outputStream);
				channel.setCharset(charset);
				channel.getAttributes().putAll(attributes);

				notificationCenter.notify(new Event(CONNECTION_ACCEPT_EVENT_NAME, channel, null));

				if (!exists(head.getMode(), head.getFile())) {
					throw new UnmappedPathException(head.getFile());
				}

				Set<Filter<Channel>> _filters = new LinkedHashSet<Filter<Channel>>(filters);
				_filters.add(BioInvocationContext.this);
				new FixedFilterChain<Channel>(_filters).go(channel);
			} catch (Throwable e) {
				logger.error("handling client {} but occur exception", client, e);
				if (channel != null && channel.getWrited() > 0) {
					return;
				}
				try {
					fail(e, head, client.getOutputStream());
				} catch (IOException ioe) {
					logger.error("", ioe);
				} finally {
					notificationCenter.notify(new Event(CONNECTION_ERROR_EVENT_NAME, channel, e));
				}
			} finally {
				logger.debug("client {} handle completed", client);
				IOToolkit.close(channel);
				IOToolkit.close(this);
				notificationCenter.notify(new Event(CONNECTION_CLOSE_EVENT_NAME, channel, null));
			}
		}

		public void close() throws IOException {
			IOToolkit.close(client);
		}

	}

}
