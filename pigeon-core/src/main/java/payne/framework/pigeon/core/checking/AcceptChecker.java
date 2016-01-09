package payne.framework.pigeon.core.checking;

import java.lang.reflect.Method;

import payne.framework.pigeon.core.exception.IllegalConfigException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;

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
 * @date 2016年1月9日 下午7:38:06
 *
 * @version 1.0.0
 */
public class AcceptChecker implements Checker {

	public boolean check(BeanFactory beanFactory, Object implementation, Class<?> interfase, Method method) throws IllegalConfigException {
		return false;
	}

}
