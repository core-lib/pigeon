package payne.framework.pigeon.core.protocol;

import java.lang.reflect.Method;
import java.util.List;

import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.processing.Step;

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
 * @date 2015年10月18日 下午4:15:19
 *
 * @version 1.0.0
 */
public interface HTTPInvocationReader {

	Invocation read(Method method, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception;

}
