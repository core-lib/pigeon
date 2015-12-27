package payne.framework.pigeon.core;

/**
 * 拦截器,用于对每次请求调用进行拦截统一处理,该拦截器采用Round Intercept 方式<br/>
 * 因此必须对拦截的{@link Invocation}调用next() 方法才会继续向下走,否则将立即返回
 * 
 * @author Payne
 * 
 */
public interface Interceptor {

	/**
	 * 拦截处理,只有在该方法里面调用了 invocation 的next()方法,远程调用才会继续运行
	 * 
	 * @param invocation
	 *            调用
	 * @return 调用结果
	 * @throws Exception
	 *             异常情况
	 */
	Object intercept(Invocation invocation) throws Exception;

}
