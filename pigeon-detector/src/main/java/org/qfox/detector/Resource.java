package org.qfox.detector;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * <p>
 * Description: Project resource interface definition, you can use method {@link Resource#getInputStream()}to get an
 * input stream of this resource
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author Payne 646742615@qq.com
 *
 * @date 2016年1月16日 上午11:57:32
 *
 * @since 1.0.0
 */
public interface Resource extends Closeable {

	/**
	 * the name of this resource
	 * 
	 * @return resource name
	 */
	String getName();

	/**
	 * the url of this resource
	 * 
	 * @return resource url
	 */
	URL getUrl();

	/**
	 * get opened input stream of this resource, if this resource did not have an opened input stream yet, it will open
	 * an input stream by using method {@link Resource#newInputStream()} and return;
	 * 
	 * @return the singleton input stream of this resource
	 * @throws IOException
	 *             when I/O error occur
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * get a newly input stream of this resource
	 * 
	 * @return newly input stream of this resource
	 * @throws IOException
	 *             when I/O error occur
	 */
	InputStream newInputStream() throws IOException;

	/**
	 * the class loader of this resource
	 * 
	 * @return class loader
	 */
	ClassLoader getClassLoader();

	/**
	 * whether the resource is a java class
	 * 
	 * @return true: if the resource is a java class, false: something else
	 */
	boolean isClass();

	/**
	 * get java type of this resource, call this method after calling {@link Resource#isClass()} and return {@link true}
	 * 
	 * @return java type
	 * 
	 * @throws RuntimeException
	 *             when the class not found by it's class loader
	 */
	Class<?> toClass();

	/**
	 * Normally, this method of resource implementation will close the input stream opened by calling method
	 * {@link Resource#getInputStream()}, but does not close the input streams opened by method
	 * {@link Resource#newInputStream()}
	 * 
	 * @throws IOException
	 *             when I/O error occur while closing the opened input stream
	 */
	void close() throws IOException;

}
