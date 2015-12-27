package payne.framework.pigeon.core;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import payne.framework.pigeon.core.exception.IllogicalInvokeException;

/**
 * 远程调用封装类,对于每一次远程请求或回应都是一个{@link Invocation}对象,里面包含着请求参数<br/>
 * 和调用结果,同时还有请求头,请求路径,请求来源客户端等信息,框架将运行结果放到result属性并将arguments属性设为null,<br/>
 * 如果此过程中发生了异常,框架将会把异常信息封装到error属性,最终返回给客户端
 * 
 * @author Payne
 * 
 */
@XmlRootElement
public class Invocation implements Serializable {
	private static final long serialVersionUID = 3185492035134672608L;

	private transient Header clientHeader;
	private transient Header serverHeader;

	private Map<String, String> properties = new TreeMap<String, String>();
	private String host;
	private int port;
	private String path;
	private Object[] arguments;
	private Object result;
	private transient Class<?> interfase;
	private transient Method method;
	private transient Object implementation;
	private transient Iterator<Interceptor> interceptors;

	/**
	 * 真正处理该远程调用,此方法调用会经过一系列包括框架和用户设定的拦截器,最终到达开放的接口方法并原路返回<br/>
	 * 需要注意的是拦截器的实现是采用round intercept的思路
	 * 
	 * @return 调用结果
	 * @throws Exception
	 *             调用过程中的异常,包括拦截器中发生的异常
	 */
	public Object invoke() throws Exception {
		if (interceptors.hasNext()) {
			return interceptors.next().intercept(this);
		} else {
			throw new IllogicalInvokeException(this);
		}
	}

	@XmlTransient
	public Header getClientHeader() {
		return clientHeader;
	}

	public void setClientHeader(Header clientHeader) {
		this.clientHeader = clientHeader;
	}

	@XmlTransient
	public Header getServerHeader() {
		return serverHeader;
	}

	public void setServerHeader(Header serverHeader) {
		this.serverHeader = serverHeader;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@XmlTransient
	public Class<?> getInterfase() {
		return interfase;
	}

	public void setInterfase(Class<?> interfase) {
		this.interfase = interfase;
	}

	@XmlTransient
	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	@XmlTransient
	public Object getImplementation() {
		return implementation;
	}

	public void setImplementation(Object implementation) {
		this.implementation = implementation;
	}

	@XmlTransient
	public Iterator<Interceptor> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(Iterator<Interceptor> interceptors) {
		this.interceptors = interceptors;
	}

	@Override
	public String toString() {
		return "Invocation [properties=" + properties + ", path=" + path + "]";
	}

}
