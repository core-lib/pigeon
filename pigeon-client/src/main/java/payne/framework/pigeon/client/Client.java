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
	protected String host;
	protected int port = 80;
	protected int timeout = 10 * 1000;
	protected String format = "application/x-java-serialized-object";
	protected ClassLoader classLoader = Client.class.getClassLoader();
	protected BeanFactory beanFactory = new SingletonBeanFactory();
	protected StreamFactory streamFactory = new InternalStreamFactory();
	protected String charset = Charset.defaultCharset().name();
	protected Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	protected List<Interceptor> interceptors = new ArrayList<Interceptor>();

	public Client(String host) {
		super();
		this.host = host;
	}

	public Client(String host, int port) {
		super();
		this.host = host;
		this.port = port;
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
