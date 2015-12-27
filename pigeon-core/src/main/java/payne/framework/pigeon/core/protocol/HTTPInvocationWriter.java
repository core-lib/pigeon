package payne.framework.pigeon.core.protocol;

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
 * @date 2015年10月18日 下午4:00:34
 *
 * @version 1.0.0
 */
public interface HTTPInvocationWriter {

	void write(Invocation invocation, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception;

}
