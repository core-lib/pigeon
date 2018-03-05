package org.qfox.detector;

import java.io.IOException;
import java.util.Collection;

/**
 * <p>
 * Description: Resource detection provider for project
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author Payne 646742615@qq.com
 *
 * @date 2016年1月16日 上午11:54:41
 *
 * @since 1.0.0
 */
public interface ResourceDetector {

	/**
	 * find resources
	 * 
	 * @param filters
	 *            resource filters
	 * @return the resources under the directory given in the constructor
	 *         arguments and also all of filters accept
	 * @throws IOException
	 *             when I/O error occur
	 */
	Collection<Resource> detect(ResourceFilter... filters) throws IOException;

	/**
	 * find resources
	 * 
	 * @param filters
	 *            resource filters
	 * @return the resources under the directory given in the constructor
	 *         arguments and also all of filters accept
	 * @throws IOException
	 *             when I/O error occur
	 */
	Collection<Resource> detect(Collection<ResourceFilter> filters) throws IOException;

	/**
	 * find resources
	 * 
	 * @param chain
	 *            resource filter chain
	 * @return the resources under the directory given in the constructor
	 *         arguments and also the chain of filters accept it
	 * @throws IOException
	 *             when I/O error occur
	 */
	Collection<Resource> detect(ResourceFilterChain chain) throws IOException;

}
