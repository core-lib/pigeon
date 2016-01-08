package payne.framework.pigeon.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;

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
	protected String host;
	protected int port;
	protected int timeout;
	protected ClassLoader classLoader;
	protected BeanFactory beanFactory;
	protected StreamFactory streamFactory;

	protected String charset = Charset.defaultCharset().name();
	protected Map<String, Object> attributes = new LinkedHashMap<String, Object>();

	public Client(String host, int port) throws IOException {
		this(host, port, "pigeon");
	}

	public Client(String host, int port, Properties properties) {
		this(host, port, new SingletonBeanFactory(properties));
	}

	public Client(String host, int port, String... beanConfigurationPaths) throws IOException {
		this(host, port, new SingletonBeanFactory(beanConfigurationPaths));
	}

	public Client(String host, int port, BeanFactory beanFactory) {
		this(host, port, beanFactory, new InternalStreamFactory());
	}

	public Client(String host, int port, StreamFactory streamFactory) throws IOException {
		this(host, port, Thread.currentThread().getContextClassLoader(), new SingletonBeanFactory(), streamFactory);
	}

	public Client(String host, int port, BeanFactory beanFactory, StreamFactory streamFactory) {
		this(host, port, Thread.currentThread().getContextClassLoader(), beanFactory, streamFactory);
	}

	public Client(String host, int port, ClassLoader classLoader, BeanFactory beanFactory, StreamFactory streamFactory) {
		this(host, port, 2 * 1000, classLoader, beanFactory, streamFactory);
	}

	public Client(String host, int port, int timeout) throws IOException {
		this(host, port, timeout, "pigeon");
	}

	public Client(String host, int port, int timeout, Properties properties) {
		this(host, port, timeout, new SingletonBeanFactory(properties));
	}

	public Client(String host, int port, int timeout, String... beanConfigurationPaths) throws IOException {
		this(host, port, timeout, new SingletonBeanFactory(beanConfigurationPaths));
	}

	public Client(String host, int port, int timeout, BeanFactory beanFactory) {
		this(host, port, timeout, beanFactory, new InternalStreamFactory());
	}

	public Client(String host, int port, int timeout, StreamFactory streamFactory) throws IOException {
		this(host, port, timeout, Thread.currentThread().getContextClassLoader(), new SingletonBeanFactory(), streamFactory);
	}

	public Client(String host, int port, int timeout, BeanFactory beanFactory, StreamFactory streamFactory) {
		this(host, port, timeout, Thread.currentThread().getContextClassLoader(), beanFactory, streamFactory);
	}

	public Client(String host, int port, int timeout, ClassLoader classLoader, BeanFactory beanFactory, StreamFactory streamFactory) {
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("port must between 0 and 65535");
		}
		if (timeout <= 0) {
			throw new IllegalArgumentException("connect timeout must larger than zero");
		}
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.classLoader = classLoader;
		this.beanFactory = beanFactory;
		this.streamFactory = streamFactory;
	}

	public <T> T create(String protocol, String format, String implementation, Class<T> interfase, Interceptor... interceptors) throws Exception {
		return create(protocol, format, implementation, interfase, new LinkedHashSet<Interceptor>(Arrays.asList(interceptors)));
	}

	public <T> T create(String protocol, String format, String implementation, Class<T> interfase, LinkedHashSet<Interceptor> interceptors) throws Exception {
		return build(protocol, format, implementation, interfase, interceptors).getProxy();
	}

	public <T> Connection<T> build(String protocol, String format, String implementation, Class<T> interfase, Interceptor... interceptors) throws Exception {
		return build(protocol, format, implementation, interfase, new LinkedHashSet<Interceptor>(Arrays.asList(interceptors)));
	}

	public <T> Connection<T> build(String protocol, String format, String implementation, Class<T> interfase, LinkedHashSet<Interceptor> interceptors) throws Exception {
		if (!beanFactory.contains(protocol)) {
			throw new UnsupportedChannelException(protocol);
		}

		if (!beanFactory.contains(format)) {
			throw new UnsupportedFormatException(format);
		}

		Connection<T> connection = new Connection<T>(this, protocol, format, implementation, interfase, new LinkedHashSet<Interceptor>(interceptors), beanFactory, streamFactory);

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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
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

}
