package payne.framework.pigeon.generation.async;

import payne.framework.pigeon.core.annotation.Open;

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
 * @date 2016年1月8日 下午9:06:41
 *
 * @version 1.0.0
 */
@Open
public interface SampleAPI {

	User login(String username, String password) throws Exception;

}
