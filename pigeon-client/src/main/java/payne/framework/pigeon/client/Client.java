package payne.framework.pigeon.client;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import payne.framework.pigeon.core.Attributed;
import payne.framework.pigeon.core.Interceptor;
import payne.framework.pigeon.core.Transcoder;
import payne.framework.pigeon.core.exception.UnsupportedChannelException;
import payne.framework.pigeon.core.exception.UnsupportedFormatException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.bean.SingletonBeanFactory;
import payne.framework.pigeon.core.factory.stream.InternalStreamFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;

public class Client implements Attributed, Transcoder {
	protected String protocol = "HTTP/1.1";
	protected final String host;
	protected final int port;
	protected int timeout = 10 * 1000;
	protected String format = "application/x-java-serialized-object";
	protected ClassLoader classLoader = Client.class.getClassLoader();
	protected BeanFactory beanFactory = new SingletonBeanFactory();
	protected StreamFactory streamFactory = new InternalStreamFactory();
	protected String charset = Charset.defaultCharset().name();
	protected Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	protected List<Interceptor> interceptors = new ArrayList<Interceptor>();

	public Client() {
		this("localhost");
	}

	public Client(String host) {
		this(host, 8080);
	}

	public Client(String host, int port) {
		super();
		if (host == null || host.trim().length() == 0) {
			throw new IllegalStateException("host must not be null or empty string");
		}
		if (port < 1 || port > 65535) {
			throw new IllegalStateException("port must between 1 and 65535");
		}
		this.host = host;
		this.port = port;
	}

	public <T> T create(Class<T> interfase) throws Exception {
		return build(interfase).getProxy();
	}

	public <T> Connection<T> build(Class<T> interfase) throws Exception {
		return build("", interfase);
	}

	public <T> T create(String implementation, Class<T> interfase) throws Exception {
		return build(implementation, interfase).getProxy();
	}

	public <T> Connection<T> build(String implementation, Class<T> interfase) throws Exception {
		if (!beanFactory.contains(protocol)) {
			throw new UnsupportedChannelException(protocol);
		}
		if (!beanFactory.contains(format)) {
			throw new UnsupportedFormatException(format);
		}
		if (classLoader == null) {
			throw new IllegalStateException("class loader must not be null");
		}
		if (beanFactory == null) {
			throw new IllegalStateException("bean factory must not be null");
		}
		if (streamFactory == null) {
			throw new IllegalStateException("stream factory must not be null");
		}
		Connection<T> connection = new Connection<T>(this, implementation, interfase);
		return connection;
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

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
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

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public List<Interceptor> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(List<Interceptor> interceptors) {
		this.interceptors = interceptors;
	}

}
