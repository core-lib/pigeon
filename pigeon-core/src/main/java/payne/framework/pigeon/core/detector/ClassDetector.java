package payne.framework.pigeon.core.detector;

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
 * @date 2015年11月8日 下午6:15:50
 *
 * @version 1.0.0
 */
public interface ClassDetector {

	Set<Class<?>> detect(ClassFilter... filters) throws ClassDetectorException;

}
