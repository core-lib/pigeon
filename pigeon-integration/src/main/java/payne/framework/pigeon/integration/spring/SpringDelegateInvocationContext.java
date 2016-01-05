package payne.framework.pigeon.integration.spring;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;

import payne.framework.pigeon.core.Constants;
import payne.framework.pigeon.core.annotation.Accept.Mode;
import payne.framework.pigeon.core.annotation.Open;
import payne.framework.pigeon.core.exception.UnmappedPathException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.filtration.Filter;
import payne.framework.pigeon.core.observation.NotificationCenter;
import payne.framework.pigeon.core.protocol.Channel;
import payne.framework.pigeon.server.DelegateInvocationContext;
import payne.framework.pigeon.server.Feature;
import payne.framework.pigeon.server.InvocationContext;
import payne.framework.pigeon.server.InvocationProcessor;
import payne.framework.pigeon.server.InvocationProcessorRegistry;
import payne.framework.pigeon.server.Status;
import payne.framework.pigeon.server.exception.ContextStartupException;
import payne.framework.pigeon.server.exception.UnregulatedInterfaceException;

public class SpringDelegateInvocationContext implements SpringInvocationContext, DelegateInvocationContext, Constants {
	private SocketAddress address = new InetSocketAddress(9090);
	private boolean autoscan = true;
	private Set<Object> managements = new HashSet<Object>();
	private Set<Filter<Channel>> filters = new LinkedHashSet<Filter<Channel>>();
	private InvocationContext delegate;

	public void bind(int port) {
		delegate.bind(port);
	}

	public void bind(SocketAddress address) {
		delegate.bind(address);
	}

	public void startup() throws ContextStartupException {
		delegate.startup();
	}

	public void shutdown() {
		delegate.shutdown();
	}

	public Status status() {
		return delegate.status();
	}

	public boolean exists(Mode mode, String path) {
		return delegate.exists(mode, path);
	}

	public InvocationProcessor lookup(Mode mode, String path) throws UnmappedPathException {
		return delegate.lookup(mode, path);
	}

	public void register(Object openable) throws UnregulatedInterfaceException {
		delegate.register(openable);
	}

	public void revoke(Object openable) {
		delegate.revoke(openable);
	}

	public void configure(Feature feature, boolean on) {
		delegate.configure(feature, on);
	}

	public boolean supports(Feature feature) {
		return delegate.supports(feature);
	}

	public String getCharset() {
		return delegate.getCharset();
	}

	public void setCharset(String charset) {
		delegate.setCharset(charset);
	}

	public InvocationContext add(Filter<Channel> filter) {
		return delegate.add(filter);
	}

	public InvocationContext remove(Filter<Channel> filter) {
		return delegate.remove(filter);
	}

	public Iterator<Filter<Channel>> filters() {
		return delegate.filters();
	}

	public Object addAttribute(String key, Object value) {
		return delegate.addAttribute(key, value);
	}

	public Object getAttribute(String key) {
		return delegate.getAttribute(key);
	}

	public Object removeAttribute(String key) {
		return delegate.removeAttribute(key);
	}

	public Map<String, Object> getAttributes() {
		return delegate.getAttributes();
	}

	public int getConcurrent() {
		return delegate.getConcurrent();
	}

	public void setConcurrent(int concurrent) {
		delegate.setConcurrent(concurrent);
	}

	public ExecutorService getExecutor() {
		return delegate.getExecutor();
	}

	public void setExecutor(ExecutorService executor) {
		delegate.setExecutor(executor);
	}

	public String[] getBeanConfigurationPaths() {
		return delegate.getBeanConfigurationPaths();
	}

	public void setBeanConfigurationPaths(String[] beanConfigurationPaths) {
		delegate.setBeanConfigurationPaths(beanConfigurationPaths);
	}

	public BeanFactory getBeanFactory() {
		return delegate.getBeanFactory();
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		delegate.setBeanFactory(beanFactory);
	}

	public StreamFactory getStreamFactory() {
		return delegate.getStreamFactory();
	}

	public void setStreamFactory(StreamFactory streamFactory) {
		delegate.setStreamFactory(streamFactory);
	}

	public InvocationProcessorRegistry getInvocationProcessorRegistry() {
		return delegate.getInvocationProcessorRegistry();
	}

	public void setInvocationProcessorRegistry(InvocationProcessorRegistry invocationProcessorRegistry) {
		delegate.setInvocationProcessorRegistry(invocationProcessorRegistry);
	}

	public int getPriority() {
		return delegate.getPriority();
	}

	public void setPriority(int priority) {
		delegate.setPriority(priority);
	}

	public NotificationCenter getNotificationCenter() {
		return delegate.getNotificationCenter();
	}

	public void setNotificationCenter(NotificationCenter notificationCenter) {
		delegate.setNotificationCenter(notificationCenter);
	}

	public void setPort(int port) {
		this.address = new InetSocketAddress(port);
	}

	public void setAddress(SocketAddress address) {
		this.address = address;
	}

	public void setAutoscan(boolean autoscan) {
		this.autoscan = autoscan;
	}

	public void setManagements(Set<Object> managements) {
		this.managements = managements;
	}

	public void setFilters(Set<Filter<Channel>> filters) {
		this.filters = filters;
	}

	public void setDelegate(InvocationContext delegate) {
		this.delegate = delegate;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		try {
			Map<String, Object> map = autoscan ? applicationContext.getBeansWithAnnotation(Open.class) : new HashMap<String, Object>();
			for (Object implementation : map.values()) {
				this.register(implementation);
			}
			for (Object management : managements) {
				this.register(management);
			}
			for (Filter<Channel> filter : filters) {
				this.add(filter);
			}
			this.bind(address);
			this.startup();
		} catch (Exception e) {
			throw new BeanCreationException(null, e);
		}
	}

}
