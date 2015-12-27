package payne.framework.pigeon.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.TreeMap;

import payne.framework.pigeon.core.Constants;
import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Interceptor;
import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.Version;
import payne.framework.pigeon.core.exception.BeanInitializeException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.processing.Step;
import payne.framework.pigeon.core.protocol.Channel;

public class InvocationProcessor implements Interceptor, Constants {
	private final Class<?> interfase;
	private final Method method;
	private final Object implementation;
	private final LinkedHashSet<Interceptor> interceptors;
	private final TreeMap<Class<? extends Annotation>, Step> processings;
	private final BeanFactory beanFactory;
	private final StreamFactory streamFactory;

	public InvocationProcessor(Class<?> interfase, Method method, Object implementation, LinkedHashSet<Interceptor> interceptors, BeanFactory beanFactory, StreamFactory streamFactory) throws BeanInitializeException {
		super();
		this.interfase = interfase;
		this.method = method;
		this.implementation = implementation;
		this.interceptors = new LinkedHashSet<Interceptor>(interceptors);
		this.interceptors.add(this);
		this.beanFactory = beanFactory;
		this.streamFactory = streamFactory;
		this.processings = Pigeons.getMethodProcessings(method);
	}

	public void process(InvocationContext context, Channel channel) throws Exception {
		Invocation invocation = channel.read(method, beanFactory, streamFactory, new ArrayList<Step>(processings.values()));
		invocation.setInterfase(interfase);
		invocation.setMethod(method);
		invocation.setImplementation(implementation);
		invocation.setInterceptors(interceptors.iterator());
		Header header = new Header();
		header.setCharset(context.getCharset());
		header.setDate(new Date());
		header.setServer(Version.getCurrent().getName() + "/" + Version.getCurrent().getCode() + "(" + System.getProperty("os.name") + " " + System.getProperty("os.version") + ")");
		invocation.setServerHeader(header);
		Object result = invocation.invoke();
		invocation.setResult(result);
		invocation.setArguments(null);
		channel.write(invocation, beanFactory, streamFactory, new ArrayList<Step>(processings.values()));
	}

	public Object intercept(Invocation invocation) throws Exception {
		try {
			return method.invoke(implementation, invocation.getArguments());
		} catch (InvocationTargetException e) {
			throw e.getCause() != null && e.getCause() instanceof Exception ? (Exception) e.getCause() : e;
		}
	}

	public Class<?> getInterfase() {
		return interfase;
	}

	public Method getMethod() {
		return method;
	}

	public Object getImplementation() {
		return implementation;
	}

	public LinkedHashSet<Interceptor> getInterceptors() {
		return interceptors;
	}

	public TreeMap<Class<? extends Annotation>, Step> getProcessings() {
		return processings;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public StreamFactory getStreamFactory() {
		return streamFactory;
	}

}
