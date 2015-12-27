package payne.framework.pigeon.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import payne.framework.pigeon.core.Constants;
import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Version;
import payne.framework.pigeon.core.exception.CodedException;
import payne.framework.pigeon.core.exception.UnmappedPathException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.bean.SingletonBeanFactory;
import payne.framework.pigeon.core.factory.stream.InternalStreamFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.filtration.Filter;
import payne.framework.pigeon.core.observation.Event;
import payne.framework.pigeon.core.observation.NotificationCenter;
import payne.framework.pigeon.core.observation.SharedNotificationCenter;
import payne.framework.pigeon.core.protocol.Channel;
import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.server.exception.ContextStartupException;
import payne.framework.pigeon.server.exception.UnregulatedInterfaceException;

public abstract class HTTPInvocationContext implements InvocationContext, Runnable, Filter<Channel>, Constants {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected SocketAddress address = new InetSocketAddress(9090);
	protected int concurrent = 100;
	protected ExecutorService executor;
	protected String[] beanConfigurationPaths = new String[] { "pigeon.properties" };
	protected BeanFactory beanFactory;
	protected StreamFactory streamFactory;
	protected InvocationProcessorRegistry invocationProcessorRegistry;
	protected Set<Object> openables = new HashSet<Object>();
	protected Status status = Status.READY;
	protected int priority = Thread.NORM_PRIORITY;
	protected Map<Feature, Boolean> features = new HashMap<Feature, Boolean>();

	protected String charset = Charset.defaultCharset().name();
	protected Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	protected Set<Filter<Channel>> filters = new LinkedHashSet<Filter<Channel>>();
	protected NotificationCenter notificationCenter = SharedNotificationCenter.getInstance();

	public void bind(int port) {
		if (status != Status.READY) {
			throw new IllegalStateException("can not bind address when invocation context has been started up");
		}
		this.address = new InetSocketAddress(port);
	}

	public void bind(SocketAddress address) {
		if (status != Status.READY) {
			throw new IllegalStateException("can not bind address when invocation context has been started up");
		}
		this.address = address;
	}

	public void startup() throws ContextStartupException {
		logger.debug("expected status is {} and current status is {}", Status.READY, status);
		if (status == Status.STARTUP) {
			throw new ContextStartupException("context " + this + " is running...");
		}
		if (status == Status.SHUTDOWN) {
			throw new ContextStartupException("context " + this + " had bean shutdown!");
		}
		try {
			beanFactory = beanFactory != null ? beanFactory : new SingletonBeanFactory(beanConfigurationPaths);
			streamFactory = streamFactory != null ? streamFactory : new InternalStreamFactory();
			invocationProcessorRegistry = invocationProcessorRegistry != null ? invocationProcessorRegistry : new HashInvocationProcessorRegistry(beanFactory, streamFactory);
			invocationProcessorRegistry.register(this);
			for (Object openable : openables) {
				invocationProcessorRegistry.register(openable);
			}
			for (Object service : invocationProcessorRegistry.services()) {
				if (service instanceof InvocationContextAware) {
					InvocationContextAware aware = (InvocationContextAware) service;
					aware.setInvocationContext(this);
				}
			}
			executor = executor != null ? executor : Executors.newFixedThreadPool(concurrent);
			start();
			notificationCenter.notify(new Event(CONTEXT_STARTUP_EVENT_NAME, this, null));
			logger.debug("invocation context startup completed on address {}", address);
		} catch (Exception e) {
			logger.error("invocation context startup failed");
			throw new ContextStartupException(e);
		}
	}

	protected void start() {
		Thread thread = new Thread(this);
		thread.setPriority(priority);
		thread.start();
	}

	public boolean exists(String path) {
		return invocationProcessorRegistry.exists(path);
	}

	public InvocationProcessor lookup(String path) throws UnmappedPathException {
		return invocationProcessorRegistry.lookup(path);
	}

	public void register(Object openable) throws UnregulatedInterfaceException {
		if (invocationProcessorRegistry != null) {
			invocationProcessorRegistry.register(openable);
		} else {
			openables.add(openable);
		}
	}

	public void revoke(Object openable) {
		if (invocationProcessorRegistry != null) {
			invocationProcessorRegistry.revoke(openable);
		} else {
			openables.remove(openable);
		}
	}

	public Status status() {
		return status;
	}

	public void configure(Feature feature, boolean on) {
		features.put(feature, on);
	}

	public boolean supports(Feature feature) {
		return features.containsKey(feature) ? features.get(feature) : feature.defaultMode;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public Iterator<Filter<Channel>> filters() {
		return filters.iterator();
	}

	public InvocationContext add(Filter<Channel> filter) {
		filters.add(filter);
		return this;
	}

	public InvocationContext remove(Filter<Channel> filter) {
		filters.remove(filter);
		return this;
	}

	public Object addAttribute(String key, Object value) {
		return attributes.put(key, value);
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public Object removeAttribute(String key) {
		return attributes.remove(key);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public int getConcurrent() {
		return concurrent;
	}

	public void setConcurrent(int concurrent) {
		this.concurrent = concurrent;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public String[] getBeanConfigurationPaths() {
		return beanConfigurationPaths;
	}

	public void setBeanConfigurationPaths(String[] beanConfigurationPaths) {
		this.beanConfigurationPaths = beanConfigurationPaths;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public StreamFactory getStreamFactory() {
		return streamFactory;
	}

	public void setStreamFactory(StreamFactory streamFactory) {
		this.streamFactory = streamFactory;
	}

	public InvocationProcessorRegistry getInvocationProcessorRegistry() {
		return invocationProcessorRegistry;
	}

	public void setInvocationProcessorRegistry(InvocationProcessorRegistry invocationProcessorRegistry) {
		this.invocationProcessorRegistry = invocationProcessorRegistry;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public NotificationCenter getNotificationCenter() {
		return notificationCenter;
	}

	public void setNotificationCenter(NotificationCenter notificationCenter) {
		this.notificationCenter = notificationCenter;
	}

	protected abstract class HTTPInvocationHandler implements Runnable, Closeable, Constants {
		protected Future<?> future;

		public final synchronized void run() {
			handle();
		}

		protected abstract void handle();

		protected void fail(Throwable exception, Head head, OutputStream out) throws IOException {
			PrintStream ps = null;
			OutputStream _out = null;
			InputStream _in = null;
			try {
				int code = 400;
				String reason = "Bad Request";
				if (exception instanceof CodedException) {
					CodedException ce = (CodedException) exception;
					code = ce.getCode();
					reason = ce.getReason();
				}

				_out = streamFactory.produce();
				exception.printStackTrace(ps = new PrintStream(_out));
				ps.flush();
				_in = streamFactory.produce(_out);

				Header header = new Header();
				header.setDate(new Date());
				header.setCharset(Charset.defaultCharset().name());
				header.setContentType("text/plain");
				header.setServer(Version.getCurrent().getName() + "/" + Version.getCurrent().getCode() + "(" + System.getProperty("os.name") + " " + System.getProperty("os.version") + ")");
				header.setContentLength(_in.available());

				IOToolkit.writeLine(head.getProtocol() + " " + code + (reason != null ? " " + reason : ""), out);
				for (Entry<String, String> entry : header.entrySet()) {
					IOToolkit.writeLine(entry.getKey() + ": " + entry.getValue(), out);
				}
				IOToolkit.writeLine("", out);

				IOToolkit.transmit(_in, out);
			} finally {
				IOToolkit.close(_in);
				IOToolkit.close(_out);
				IOToolkit.close(ps);
			}
		}

		public final void bind(Future<?> future) {
			this.future = future;
		}

	}

}
