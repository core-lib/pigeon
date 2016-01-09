package payne.framework.pigeon.core.checking;

import java.lang.reflect.Method;

import payne.framework.pigeon.core.exception.IllegalConfigException;

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
 * @date 2016年1月9日 下午6:43:22
 *
 * @version 1.0.0
 */
public interface Checker {

	boolean check(Object implementation, Class<?> interfase, Method method) throws IllegalConfigException;

}
