package payne.framework.pigeon.server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import payne.framework.pigeon.core.Interceptor;
import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.annotation.Accept;
import payne.framework.pigeon.core.annotation.Accept.Mode;
import payne.framework.pigeon.core.exception.UnmappedPathException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.toolkit.Collections;
import payne.framework.pigeon.server.exception.DuplicatePathException;
import payne.framework.pigeon.server.exception.InvalidPathException;
import payne.framework.pigeon.server.exception.UnregulatedInterfaceException;

public class HashInvocationProcessorRegistry implements InvocationProcessorRegistry {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Map<String, InvocationProcessor> map = new HashMap<String, InvocationProcessor>();
	private final BeanFactory beanFactory;
	private final StreamFactory streamFactory;

	public HashInvocationProcessorRegistry(BeanFactory beanFactory, StreamFactory streamFactory) {
		this.beanFactory = beanFactory;
		this.streamFactory = streamFactory;
	}

	public Iterator<Registration> iterator() {
		Set<Registration> registrations = new HashSet<Registration>();
		for (Entry<String, InvocationProcessor> entry : map.entrySet()) {
			registrations.add(new Registration(entry.getKey(), entry.getValue()));
		}
		return registrations.iterator();
	}

	public Set<String> paths() {
		return map.keySet();
	}

	public Collection<InvocationProcessor> processors() {
		return map.values();
	}

	public Set<Registration> matches(String regex) {
		Set<Registration> pairs = new HashSet<Registration>();
		Iterator<Registration> iterator = this.iterator();
		while (iterator.hasNext()) {
			Registration pair = iterator.next();
			if (pair.getPath().matches(regex)) {
				pairs.add(pair);
			}
		}
		return pairs;
	}

	public boolean exists(String path) {
		return map.containsKey(path.trim().replaceAll("/+", "/"));
	}

	public InvocationProcessor lookup(String path) throws UnmappedPathException {
		if (path == null || !exists(path)) {
			throw new UnmappedPathException(path);
		}
		return map.get(path.trim().replaceAll("/+", "/"));
	}

	public synchronized void register(Object service) throws UnregulatedInterfaceException {
		logger.info("register open service [{}]", service);

		Set<Class<?>> interfaces = Pigeons.getOpenableInterfaces(service.getClass());

		LinkedHashSet<Class<? extends Interceptor>> classes = Pigeons.getClassAllInterceptors(service.getClass());
		LinkedHashSet<Interceptor> interceptors = new LinkedHashSet<Interceptor>();
		for (Class<? extends Interceptor> clazz : classes) {
			Interceptor interceptor = beanFactory.get(clazz);
			interceptors.add(interceptor);
		}
		logger.info("it binds [{}] open interface(s) list of {}", interfaces.size(), interfaces);
		for (Class<?> interfase : interfaces) {

			logger.info("start building open [{}]", interfase);

			Set<Method> methods = Pigeons.getInterfaceDeclaredOpenableMethods(interfase);

			for (Method method : methods) {
				// 必须声明有 IOException Exception 或 Throwable
				if (!Collections.containsAny(Arrays.asList(method.getExceptionTypes()), IOException.class, Exception.class, Throwable.class)) {
					throw new UnregulatedInterfaceException("open method " + method + " declared in " + interfase + " isn't declared with any of IOException Exception or Throwable", interfase, method);
				}

				String x = Pigeons.getOpenPath(service);
				String y = Pigeons.getOpenPath(interfase);
				String z = Pigeons.getOpenPath(method);

				String path = Pigeons.getOpenPath(x + y + z);

				logger.info("opening method [{}] to path [{}]", method, path);

				if (!Pigeons.isPathValidate(path)) {
					throw new InvalidPathException(path, interfase, method);
				}
				if (map.containsKey(path)) {
					throw new DuplicatePathException(path, interfase, method);
				}

				Accept accept = method.isAnnotationPresent(Accept.class) ? method.getAnnotation(Accept.class) : interfase.isAnnotationPresent(Accept.class) ? interfase.getAnnotation(Accept.class) : null;
				// 默认情况下所有的请求方式都是接受的
				Mode[] modes = accept != null && accept.modes().length > 0 ? accept.modes() : Mode.values();
				String[] media = accept != null ? accept.media() : new String[0];

				try {
					map.put(path, new InvocationProcessor(Arrays.asList(modes), Arrays.asList(media), interfase, method, service, interceptors, beanFactory, streamFactory));
				} catch (Exception e) {
					throw new UnregulatedInterfaceException(e, interfase, method);
				}
			}

			logger.info("open interface [{}] completed", interfase);

		}

		logger.info("open service [{}] completed", service);

	}

	public synchronized void revoke(Object service) throws IllegalArgumentException {
		logger.info("revoke open service [{}]", service);
		Set<Class<?>> interfaces = Pigeons.getOpenableInterfaces(service.getClass());
		for (Class<?> interfase : interfaces) {
			Set<Method> methods = Pigeons.getInterfaceDeclaredOpenableMethods(interfase);
			for (Method method : methods) {
				String x = Pigeons.getOpenPath(service);
				String y = Pigeons.getOpenPath(interfase);
				String z = Pigeons.getOpenPath(method);
				String path = Pigeons.getOpenPath(x + y + z);
				logger.info("revoke open path [{}]", path);
				map.remove(path);
			}
		}
		logger.info("revoke open service [{}] completed", service);
	}

	public Set<Object> services() {
		Set<Object> services = new HashSet<Object>();
		Iterator<Registration> iterator = iterator();
		while (iterator.hasNext()) {
			Registration registration = iterator.next();
			services.add(registration.getProcessor().getImplementation());
		}
		return services;
	}

	public void clear() {
		map.clear();
	}

}
