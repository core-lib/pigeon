package payne.framework.pigeon.server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
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
import payne.framework.pigeon.core.Path;
import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.annotation.Accept;
import payne.framework.pigeon.core.annotation.Accept.Mode;
import payne.framework.pigeon.core.exception.UnmappedPathException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.toolkit.Collections;
import payne.framework.pigeon.server.exception.DuplicatePathException;
import payne.framework.pigeon.server.exception.UnregulatedInterfaceException;

public class DefaultInvocationProcessorRegistry implements InvocationProcessorRegistry {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Map<Mode, Map<Path, InvocationProcessor>> map = new HashMap<Mode, Map<Path, InvocationProcessor>>();
	private final BeanFactory beanFactory;
	private final StreamFactory streamFactory;

	public DefaultInvocationProcessorRegistry(BeanFactory beanFactory, StreamFactory streamFactory) {
		this.beanFactory = beanFactory;
		this.streamFactory = streamFactory;
	}

	public Iterator<Registration> iterator() {
		Set<Registration> registrations = new HashSet<Registration>();
		for (Entry<Mode, Map<Path, InvocationProcessor>> entry : map.entrySet()) {
			for (Entry<Path, InvocationProcessor> e : entry.getValue().entrySet()) {
				registrations.add(new Registration(e.getKey(), e.getValue()));
			}
		}
		return registrations.iterator();
	}

	public Set<Path> paths() {
		Set<Path> paths = new HashSet<Path>();
		for (Entry<Mode, Map<Path, InvocationProcessor>> entry : map.entrySet()) {
			paths.addAll(entry.getValue().keySet());
		}
		return paths;
	}

	public Set<InvocationProcessor> processors() {
		Set<InvocationProcessor> processors = new HashSet<InvocationProcessor>();
		for (Entry<Mode, Map<Path, InvocationProcessor>> entry : map.entrySet()) {
			processors.addAll(entry.getValue().values());
		}
		return processors;
	}

	public Set<Registration> matches(String regex) {
		Set<Registration> pairs = new HashSet<Registration>();
		Iterator<Registration> iterator = this.iterator();
		while (iterator.hasNext()) {
			Registration pair = iterator.next();
			if (pair.getPath().getPattern().matcher(regex).matches()) {
				pairs.add(pair);
			}
		}
		return pairs;
	}

	public boolean exists(Mode mode, String path) {
		if (mode == null || path == null) {
			return false;
		}
		Map<Path, InvocationProcessor> map = this.map.containsKey(mode) ? this.map.get(mode) : new HashMap<Path, InvocationProcessor>();
		path = path.trim().replaceAll("/+", "/");
		boolean contained = map.containsKey(path);
		if (contained) {
			return true;
		}
		for (Path p : map.keySet()) {
			if (p.getDefinition().equals(path) || p.getPattern().matcher(path).matches()) {
				return true;
			}
		}
		return false;
	}

	public InvocationProcessor lookup(Mode mode, String path) throws UnmappedPathException {
		if (mode == null || path == null) {
			throw new UnmappedPathException(path);
		}
		Map<Path, InvocationProcessor> map = this.map.containsKey(mode) ? this.map.get(mode) : new HashMap<Path, InvocationProcessor>();
		path = path.trim().replaceAll("/+", "/");
		InvocationProcessor processor = map.get(path);
		if (processor != null) {
			return processor;
		}
		for (Entry<Path, InvocationProcessor> entry : map.entrySet()) {
			if (entry.getKey().getDefinition().equals(path) || entry.getKey().getPattern().matcher(path).matches()) {
				return entry.getValue();
			}
		}
		throw new UnmappedPathException(path);
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
				String x = Pigeons.getOpenPath(service);
				String y = Pigeons.getOpenPath(interfase);
				String z = Pigeons.getOpenPath(method);

				String definition = Pigeons.getOpenPath(x + y + z);

				logger.info("opening method [{}] to path [{}]", method, definition);

				Accept accept = method.isAnnotationPresent(Accept.class) ? method.getAnnotation(Accept.class) : interfase.isAnnotationPresent(Accept.class) ? interfase.getAnnotation(Accept.class) : null;
				// 默认情况下所有的请求方式都是接受的
				Mode[] modes = accept != null && accept.modes().length > 0 ? accept.modes() : Mode.values();
				String[] media = accept != null ? accept.media() : new String[0];

				try {
					for (Mode mode : modes) {
						Map<Path, InvocationProcessor> m = map.get(mode);
						if (m == null) {
							map.put(mode, m = new HashMap<Path, InvocationProcessor>());
						}
						Path path = new Path(definition, mode);
						// 检查是否重复
						if (m.containsKey(path)) {
							throw new DuplicatePathException(definition, interfase, method);
						}
						InvocationProcessor processor = new InvocationProcessor(path, Arrays.asList(modes), Arrays.asList(media), interfase, method, service, interceptors, beanFactory, streamFactory);
						m.put(path, processor);
					}
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
