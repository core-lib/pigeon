package payne.framework.pigeon.client;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;

import payne.framework.pigeon.client.exception.NonopenMethodException;
import payne.framework.pigeon.core.Callback;
import payne.framework.pigeon.core.Interceptor;
import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;

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
 * @date 2015年8月5日 上午10:15:11
 *
 * @version 1.0.0
 */
public class AsyncConnection<T> extends Connection<T> {

	public AsyncConnection(AsyncClient client, String protocol, String format, String implementation, Class<T> i, LinkedHashSet<Interceptor> is, BeanFactory beanFactory, StreamFactory sf) throws Exception {
		super(client, protocol, format, implementation, i, is, beanFactory, sf);
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] arguments) throws Throwable {
		if (!Pigeons.isOpenableMethod(method)) {
			// 如果是 Object 的方法 那么可以直接调用不会通过网络进行远程调用
			if (method.getDeclaringClass() == Object.class) {
				return method.invoke(this, arguments);
			}
			throw new NonopenMethodException(interfase, method, arguments);
		}
		AsyncClient ac = (AsyncClient) client;
		ac.getExecutor().execute(new Runnable() {

			public void run() {
				@SuppressWarnings("unchecked")
				Callback<Object> callback = (Callback<Object>) arguments[arguments.length - 1];
				callback = callback == null ? Callback.DEFAULT : callback;
				Object result = null;
				Throwable throwable = null;
				try {
					result = AsyncConnection.super.invoke(proxy, method, Arrays.copyOf(arguments, arguments.length - 1));
				} catch (Throwable e) {
					throwable = e;
				}
				try {
					if (throwable == null) {
						callback.onSuccess(result);
					} else {
						callback.onFail(throwable);
					}
				} finally {
					callback.onCompleted(throwable == null, result, throwable);
				}
			}

		});
		return null;
	}

}
