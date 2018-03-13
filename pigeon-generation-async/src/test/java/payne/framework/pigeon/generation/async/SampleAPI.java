package payne.framework.pigeon.generation.async;

import payne.framework.pigeon.core.annotation.Open;
import payne.framework.pigeon.generation.annotation.Name;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 *
 * @author yangchangpei 646742615@qq.com
 * @version 1.0.0
 * @date 2016年1月8日 下午9:06:41
 */
@Open("/sample")
public interface SampleAPI {

    /**
     * 登录接口
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     * @throws Exception 异常
     */
    @Open("/login")
    User login(@Name("username") String username, @Name("password") String password) throws Exception;

    /**
     * 登出接口
     *
     * @throws Exception 异常
     */
    @Open("/logout")
    void logout() throws Exception;

}
