package org.qfox.detector;

/**
 * <p>
 * Description: Customize your resource filter by implements this interface
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author Payne 646742615@qq.com
 *
 * @date 2016年1月16日 下午12:18:19
 *
 * @since 1.0.0
 */
public interface ResourceFilter {

	/**
	 * determine the resource is accept in this resource filter, when accepted,
	 * call chain's method {@link ResourceFilterChain#doNext(Resource)}, or
	 * return false, never return true!!! in case of the next filters does not
	 * accept it!
	 * 
	 * @param resource
	 *            the resource under filtering
	 * @param chain
	 *            filter chain
	 * @return true if all of filters were accept it or else false
	 */
	boolean accept(Resource resource, ResourceFilterChain chain);

}
