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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import payne.framework.pigeon.server.exception.UnregulatedInterfaceException;

public class DefaultInvocationProcessorRegistry implements InvocationProcessorRegistry {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Map<Mode, Map<Pattern, InvocationProcessor>> map = new HashMap<Mode, Map<Pattern, InvocationProcessor>>();
	private final BeanFactory beanFactory;
	private final StreamFactory streamFactory;

	public DefaultInvocationProcessorRegistry(BeanFactory beanFactory, StreamFactory streamFactory) {
		this.beanFactory = beanFactory;
		this.streamFactory = streamFactory;
	}

	public Iterator<Registration> iterator() {
		Set<Registration> registrations = new HashSet<Registration>();
		for (Entry<Mode, Map<Pattern, InvocationProcessor>> entry : map.entrySet()) {
			for (Entry<Pattern, InvocationProcessor> e : entry.getValue().entrySet()) {
				registrations.add(new Registration(entry.getKey(), e.getKey(), e.getValue()));
			}
		}
		return registrations.iterator();
	}

	public Set<Path> paths() {
		Set<Path> paths = new HashSet<Path>();
		for (Entry<Mode, Map<Pattern, InvocationProcessor>> entry : map.entrySet()) {
			for (Entry<Pattern, InvocationProcessor> e : entry.getValue().entrySet()) {
				paths.add(new Path(entry.getKey(), e.getKey()));
			}
		}
		return paths;
	}

	public Set<InvocationProcessor> processors() {
		Set<InvocationProcessor> processors = new HashSet<InvocationProcessor>();
		for (Entry<Mode, Map<Pattern, InvocationProcessor>> entry : map.entrySet()) {
			for (Entry<Pattern, InvocationProcessor> e : entry.getValue().entrySet()) {
				processors.add(e.getValue());
			}
		}
		return processors;
	}

	public Set<Registration> matches(String regex) {
		Set<Registration> pairs = new HashSet<Registration>();
		Iterator<Registration> iterator = this.iterator();
		while (iterator.hasNext()) {
			Registration pair = iterator.next();
			if (pair.getPattern().matcher(regex).matches()) {
				pairs.add(pair);
			}
		}
		return pairs;
	}

	public boolean exists(Mode mode, String path) {
		Map<Pattern, InvocationProcessor> map = this.map.containsKey(mode) ? this.map.get(mode) : new HashMap<Pattern, InvocationProcessor>();
		path = path.trim().replaceAll("/+", "/");
		boolean contained = map.containsKey(path);
		if (contained) {
			return true;
		}
		for (Pattern pattern : map.keySet()) {
			if (pattern.matcher(path).matches()) {
				return true;
			}
		}
		return false;
	}

	public InvocationProcessor lookup(Mode mode, String path) throws UnmappedPathException {
		if (path == null) {
			throw new UnmappedPathException(path);
		}
		Map<Pattern, InvocationProcessor> map = this.map.containsKey(mode) ? this.map.get(mode) : new HashMap<Pattern, InvocationProcessor>();
		path = path.trim().replaceAll("/+", "/");
		InvocationProcessor processor = map.get(path);
		if (processor != null) {
			return processor;
		}
		for (Entry<Pattern, InvocationProcessor> entry : map.entrySet()) {
			Pattern pattern = entry.getKey();
			if (pattern.matcher(path).matches()) {
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
				// 必须声明有 IOException Exception 或 Throwable
				if (!Collections.containsAny(Arrays.asList(method.getExceptionTypes()), IOException.class, Exception.class, Throwable.class)) {
					throw new UnregulatedInterfaceException("open method " + method + " declared in " + interfase + " isn't declared with any of IOException Exception or Throwable", interfase, method);
				}

				String x = Pigeons.getOpenPath(service);
				String y = Pigeons.getOpenPath(interfase);
				String z = Pigeons.getOpenPath(method);

				String path = Pigeons.getOpenPath(x + y + z);

				logger.info("opening method [{}] to path [{}]", method, path);

				// 分析路径将自定义的路径表达式转换成真正的正则表达式
				Pattern pattern = Pattern.compile("\\{(?:(\\w+)\\:)?(.*?)\\}");
				Matcher matcher = pattern.matcher(path);
				String regex = path;
				while (matcher.find()) {
					String name = matcher.group(1);
					String regular = matcher.group(2);
					regex = regex.replace(matcher.group(), name != null ? regular : "[^/]*");
				}

				Accept accept = method.isAnnotationPresent(Accept.class) ? method.getAnnotation(Accept.class) : interfase.isAnnotationPresent(Accept.class) ? interfase.getAnnotation(Accept.class) : null;
				// 默认情况下所有的请求方式都是接受的
				Mode[] modes = accept != null && accept.modes().length > 0 ? accept.modes() : Mode.values();
				String[] media = accept != null ? accept.media() : new String[0];

				try {
					Pattern p = Pattern.compile(regex);
					InvocationProcessor processor = new InvocationProcessor(path, p, Arrays.asList(modes), Arrays.asList(media), interfase, method, service, interceptors, beanFactory, streamFactory);
					for (Mode mode : modes) {
						Map<Pattern, InvocationProcessor> m = map.get(mode);
						if (m == null) {
							map.put(mode, m = new HashMap<Pattern, InvocationProcessor>());
						}
						// 检查是否重复
						for (Pattern key : m.keySet()) {
							if (key.pattern().equals(p.pattern())) {
								throw new DuplicatePathException(path, interfase, method);
							}
						}
						m.put(p, processor);
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
