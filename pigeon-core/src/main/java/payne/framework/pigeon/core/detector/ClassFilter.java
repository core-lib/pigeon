package payne.framework.pigeon.core.detector;

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
 * @date 2016年1月8日 上午11:01:55
 *
 * @version 1.0.0
 */
public interface ClassFilter {

	boolean accept(Class<?> clazz);

}
