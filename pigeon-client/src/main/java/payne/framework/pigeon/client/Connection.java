package payne.framework.pigeon.client;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import payne.framework.pigeon.client.exception.NonopenMethodException;
import payne.framework.pigeon.core.Constants;
import payne.framework.pigeon.core.Framework;
import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Interceptor;
import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.annotation.Correspond;
import payne.framework.pigeon.core.annotation.Open;
import payne.framework.pigeon.core.exception.RemoteMethodException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.filtration.Filter;
import payne.framework.pigeon.core.filtration.FilterChain;
import payne.framework.pigeon.core.filtration.FilterManager;
import payne.framework.pigeon.core.filtration.FixedFilterChain;
import payne.framework.pigeon.core.processing.Step;
import payne.framework.pigeon.core.protocol.Channel;
import payne.framework.pigeon.core.protocol.State;
import payne.framework.pigeon.core.toolkit.IOToolkit;

public class Connection<T> implements InvocationHandler, Interceptor, Filter<Channel>, FilterManager<Channel, Connection<T>>, Constants {
	protected final Client client;
	protected final String protocol;
	protected final String format;
	protected final String host;
	protected final int port;
	protected final int timeout;
	protected final String charset;
	protected final String implementation;
	protected final Class<T> interfase;
	protected final List<Interceptor> interceptors;
	protected final Open open;
	protected final BeanFactory beanFactory;
	protected final Map<Method, TreeMap<Class<? extends Annotation>, Step>> processings;
	protected final Map<Method, Method> correspondences;
	protected final StreamFactory streamFactory;
	protected final Set<Filter<Channel>> filters = new LinkedHashSet<Filter<Channel>>();
	protected final T proxy;

	public Connection(Client client, String implementation, Class<T> interfase) throws Exception {
		super();
		this.client = client;
		this.protocol = client.protocol;
		this.format = client.format;
		this.host = client.host;
		this.port = client.port;
		this.timeout = client.timeout > 0 ? client.timeout : 0;
		this.charset = client.charset != null && client.charset.trim().length() > 0 ? client.charset.trim() : Charset.defaultCharset().name();
		this.implementation = implementation != null ? implementation.trim() : "";
		this.interfase = interfase;
		this.interceptors = client.interceptors != null ? new ArrayList<Interceptor>(client.interceptors) : new ArrayList<Interceptor>();
		this.interceptors.add(this);
		this.beanFactory = client.beanFactory;
		this.streamFactory = client.streamFactory;
		this.open = interfase.getAnnotation(Open.class);
		this.processings = new HashMap<Method, TreeMap<Class<? extends Annotation>, Step>>();
		this.correspondences = new HashMap<Method, Method>();
		Set<Method> set = interfase.isAnnotationPresent(Correspond.class) ? Pigeons.getInterfaceDeclaredOpenableMethods(interfase.getAnnotation(Correspond.class).value()) : new HashSet<Method>();
		Map<String, Method> map = new HashMap<String, Method>();
		for (Method method : set) {
			String path = Pigeons.getOpenPath(method);
			map.put(path, method);
		}
		Set<Method> methods = Pigeons.getClassAllOpenableMethods(interfase);
		for (Method method : methods) {
			processings.put(method, Pigeons.getMethodProcessings(method));
			String path = Pigeons.getOpenPath(method);
			correspondences.put(method, map.containsKey(path) ? map.get(path) : method);
		}
		this.proxy = interfase.cast(Proxy.newProxyInstance(client.getClassLoader(), new Class<?>[] { interfase }, this));
	}

	public void filtrate(Channel channel, FilterChain<Channel> chain) throws Exception {
		Invocation request = (Invocation) channel.getAttribute(CHANNEL_INVOCATION_ATTRIBUTE_KEY);
		Collection<Step> steps = processings.get(request.getMethod()).values();
		channel.send(null, request, beanFactory, streamFactory, new ArrayList<Step>(steps));
		State status = channel.getStatus();
		switch (status.getCode()) {
		case HttpURLConnection.HTTP_OK: {
			Method method = correspondences.get(request.getMethod());
			Invocation response = channel.receive(null, method, beanFactory, streamFactory, new ArrayList<Step>(steps));
			request.setServerHeader(response.getServerHeader());
			request.setResult(response.getResult());
			break;
		}
		default:
			throw new RemoteMethodException(channel.getStatus().getCode(), channel.getStatus().getMessage(), IOToolkit.toString(channel));
		}
	}

	public Object intercept(Invocation invocation) throws Exception {
		Channel channel = null;
		try {
			channel = beanFactory.establish(protocol, Channel.class);
			channel.initialize(host, port, invocation.getPath(), timeout, format);

			channel.getAttributes().putAll(client.getAttributes());
			channel.addAttribute(CHANNEL_INVOCATION_ATTRIBUTE_KEY, invocation);

			Set<Filter<Channel>> _filters = new LinkedHashSet<Filter<Channel>>();
			_filters.add(this);
			new FixedFilterChain<Channel>(_filters).go(channel);

			return invocation.getResult();
		} catch (Exception e) {
			throw e;
		} finally {
			IOToolkit.close(channel);
		}
	}

	public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
		if (!Pigeons.isOpenableMethod(method)) {
			// 如果是 Object 的方法 那么可以直接调用不会通过网络进行远程调用
			if (method.getDeclaringClass() == Object.class) {
				return method.invoke(this, arguments);
			}
			throw new NonopenMethodException(interfase, method, arguments);
		}
		try {
			String x = Pigeons.getOpenPath(implementation);
			String y = Pigeons.getOpenPath(interfase);
			String z = Pigeons.getOpenPath(method);

			String path = Pigeons.getOpenPath(x + y + z);

			Header header = new Header();
			header.setContentType(format);
			header.setCharset(charset);
			header.setConnection("closed");
			header.setHost(host + (port == 80 ? "" : ":" + port));
			header.setPragma("no-cache");
			header.setCacheControl("no-cache");
			String fwname = Framework.getCurrent().getName();
			String fwversion = Framework.getCurrent().getVersion();
			String language = System.getProperty("user.language");
			String osname = System.getProperty("os.name");
			String osversion = System.getProperty("os.version");
			header.setUserAgent(fwname + "/" + fwversion + "[" + language + "]" + "(" + osname + " " + osversion + ")");

			Invocation invocation = new Invocation();
			invocation.setClientHeader(header);
			invocation.setHost(host);
			invocation.setPort(port);
			invocation.setPath(path);
			invocation.setInterfase(interfase);
			invocation.setMethod(method);
			invocation.setImplementation(this);
			invocation.setArguments(arguments);
			invocation.setInterceptors(interceptors.iterator());

			return invocation.invoke();
		} catch (Throwable e) {
			throw e instanceof IOException ? (IOException) e : new IOException(e);
		}
	}

	public Connection<T> add(Filter<Channel> filter) {
		filters.add(filter);
		return this;
	}

	public Connection<T> remove(Filter<Channel> filter) {
		filters.remove(filter);
		return this;
	}

	public Iterator<Filter<Channel>> filters() {
		return filters.iterator();
	}

	public Client getClient() {
		return client;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getFormat() {
		return format;
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

	public String getCharset() {
		return charset;
	}

	public String getImplementation() {
		return implementation;
	}

	public Class<T> getInterfase() {
		return interfase;
	}

	public List<Interceptor> getInterceptors() {
		return interceptors;
	}

	public Open getOpen() {
		return open;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public Map<Method, TreeMap<Class<? extends Annotation>, Step>> getProcessings() {
		return processings;
	}

	public Map<Method, Method> getCorrespondences() {
		return correspondences;
	}

	public StreamFactory getStreamFactory() {
		return streamFactory;
	}

	public Set<Filter<Channel>> getFilters() {
		return filters;
	}

	public T getProxy() {
		return proxy;
	}

}
