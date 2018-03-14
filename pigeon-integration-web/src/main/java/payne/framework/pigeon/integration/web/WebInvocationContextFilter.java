package payne.framework.pigeon.integration.web;

import org.qfox.detector.*;
import org.qfox.detector.DefaultResourceDetector.Builder;
import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.annotation.Accept.Mode;
import payne.framework.pigeon.core.exception.UnsupportedChannelException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.bean.SingletonBeanFactory;
import payne.framework.pigeon.core.factory.stream.InternalStreamFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.filtration.FixedFilterChain;
import payne.framework.pigeon.core.observation.Event;
import payne.framework.pigeon.core.protocol.Channel;
import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.server.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

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
public class WebInvocationContextFilter extends HTTPInvocationContext implements InvocationContext, Filter, ResourceFilter {
	public static final String PACKAGE = "package";
	public static final String RECURSIVE = "recursive";
	public static final String BEAN_FACTORY = "bean-factory";
	public static final String STREAM_FACTORY = "stream-factory";
	public static final String REGISTRY = "registry";
	public static final String CHARSET = "charset";

	protected ResourceDetector detector;

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
			Builder builder = DefaultResourceDetector.Builder.scan(root);
			builder = recursive ? builder.recursively() : builder.unrecursive();
			detector = builder.build();
			Collection<Resource> resources = detector.detect(this);
			for (Resource resource : resources) {
				Class<?> clazz = resource.toClass();
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
		String file = request.getServletPath();
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

	public boolean accept(Resource resource, ResourceFilterChain chain) {
		if (resource.isClass() == false) {
			return false;
		}
		Class<?> clazz;
		try {
			clazz = resource.toClass();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return (Pigeons.isOpenableClass(clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) && chain.doNext(resource);
	}

	public void run() {
		throw new UnsupportedOperationException();
	}

	public void shutdown() {
		throw new UnsupportedOperationException();
	}

}
