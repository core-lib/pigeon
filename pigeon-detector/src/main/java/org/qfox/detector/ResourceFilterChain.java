package org.qfox.detector;

/**
 * <p>
 * Description: Make a chain for resource filters
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author Payne 646742615@qq.com
 *
 * @date 2016年1月16日 下午3:46:33
 *
 * @since 1.0.0
 */
public interface ResourceFilterChain {

	/**
	 * do next filtration
	 * 
	 * @param resource
	 *            the resource under filtering
	 * @return true if all of filters were accept it or else false
	 */
	boolean doNext(Resource resource);

	/**
	 * reset the filter chain for reuse
	 * 
	 * @return self of filter chain
	 */
	ResourceFilterChain reset();

}
