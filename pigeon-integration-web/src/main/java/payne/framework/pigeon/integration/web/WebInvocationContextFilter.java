package payne.framework.pigeon.integration.web;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.annotation.Accept.Mode;
import payne.framework.pigeon.core.detector.ClassDetector;
import payne.framework.pigeon.core.detector.ClassFilter;
import payne.framework.pigeon.core.detector.SimpleClassDetector;
import payne.framework.pigeon.core.exception.UnsupportedChannelException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.bean.SingletonBeanFactory;
import payne.framework.pigeon.core.factory.stream.InternalStreamFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.filtration.FixedFilterChain;
import payne.framework.pigeon.core.observation.Event;
import payne.framework.pigeon.core.protocol.Channel;
import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.server.DefaultInvocationProcessorRegistry;
import payne.framework.pigeon.server.HTTPInvocationContext;
import payne.framework.pigeon.server.InvocationContext;
import payne.framework.pigeon.server.InvocationContextAware;
import payne.framework.pigeon.server.InvocationProcessorRegistry;

/**
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author yangchangpei 646742615@qq.com
 *
 * @date 2015年11月8日 下午7:27:55
 *
 * @version 1.0.0
 */
public class WebInvocationContextFilter extends HTTPInvocationContext implements InvocationContext, Filter, ClassFilter {
	public static final String PACKAGE = "package";
	public static final String RECURSIVE = "recursive";
	public static final String BEAN_FACTORY = "bean-factory";
	public static final String STREAM_FACTORY = "stream-factory";
	public static final String REGISTRY = "registry";
	public static final String CHARSET = "charset";

	protected ClassDetector detector;

	public void init(FilterConfig config) throws ServletException {
		String root = config.getInitParameter(PACKAGE);
		if (root == null) {
			throw new ServletException("unspecified detect package in the filter init parameters");
		}
		boolean recursive = config.getInitParameter(RECURSIVE) == null ? true : Boolean.valueOf(config.getInitParameter(RECURSIVE));
		try {
			this.charset = config.getInitParameter(CHARSET) == null ? "UTF-8" : config.getInitParameter(CHARSET);
			beanFactory = config.getInitParameter(BEAN_FACTORY) == null ? new SingletonBeanFactory() : (BeanFactory) Class.forName(config.getInitParameter(BEAN_FACTORY)).newInstance();
			streamFactory = config.getInitParameter(STREAM_FACTORY) == null ? new InternalStreamFactory() : (StreamFactory) Class.forName(config.getInitParameter(STREAM_FACTORY)).newInstance();
			String registry = config.getInitParameter(REGISTRY);
			invocationProcessorRegistry = registry == null ? new DefaultInvocationProcessorRegistry(beanFactory, streamFactory) : (InvocationProcessorRegistry) Class.forName(registry).newInstance();
			detector = new SimpleClassDetector(root, recursive);
			Set<Class<?>> classes = detector.detect(this);
			for (Class<?> clazz : classes) {
				Object implementation = beanFactory.get(clazz);
				invocationProcessorRegistry.register(implementation);
				if (implementation instanceof InvocationContextAware) {
					InvocationContextAware aware = (InvocationContextAware) implementation;
					aware.setInvocationContext(this);
				}
			}
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			config.getServletContext().setAttribute(WEB_INVOCATION_CONTEXT_ATTRIBUTE_KEY, this);
		}
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		Channel channel = null;
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		Mode mode = Mode.likeOf(request.getMethod());
		String file = request.getRequestURI();
		if (!exists(mode, file)) {
			chain.doFilter(request, response);
			return;
		}

		try {
			String parameter = request.getQueryString() == null ? "" : request.getQueryString();
			String protocol = request.getProtocol();
			SocketAddress address = new InetSocketAddress(request.getRemoteHost(), request.getRemotePort());

			if (!beanFactory.contains(protocol)) {
				throw new UnsupportedChannelException(protocol);
			}

			channel = beanFactory.establish(protocol, Channel.class);
			channel.initialize(protocol, mode, file, parameter, address, new HttpServletRequestInputStream(request), new HttpServletResponseOutputStream(response));
			channel.setCharset(charset);
			channel.getAttributes().putAll(attributes);

			notificationCenter.notify(new Event(CONNECTION_ACCEPT_EVENT_NAME, channel, null));

			Set<payne.framework.pigeon.core.filtration.Filter<Channel>> _filters = new LinkedHashSet<payne.framework.pigeon.core.filtration.Filter<Channel>>(filters);
			_filters.add(this);
			new FixedFilterChain<Channel>(_filters).go(channel);
		} catch (Throwable e) {
			throw new ServletException(e);
		} finally {
			logger.debug("client {} handle completed", servletRequest);
			IOToolkit.close(channel);
			notificationCenter.notify(new Event(CONNECTION_CLOSE_EVENT_NAME, channel, null));
		}
	}

	public void destroy() {
		return;
	}

	public boolean accept(Class<?> clazz) {
		return Pigeons.isOpenableClass(clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
	}

	public void run() {
		throw new UnsupportedOperationException();
	}

	public void shutdown() {
		throw new UnsupportedOperationException();
	}

}
