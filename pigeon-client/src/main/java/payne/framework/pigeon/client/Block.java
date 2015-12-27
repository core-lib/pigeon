package payne.framework.pigeon.client;

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
 * @date 2015年8月4日 下午5:48:52
 *
 * @version 1.0.0
 */
public interface Block<P, R> {

	R call(P... parameters) throws Throwable;

}
