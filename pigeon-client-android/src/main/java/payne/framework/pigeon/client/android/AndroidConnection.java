package payne.framework.pigeon.client.android;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;

import payne.framework.pigeon.client.Connection;
import payne.framework.pigeon.client.exception.NonopenMethodException;
import payne.framework.pigeon.core.Callback;
import payne.framework.pigeon.core.Interceptor;
import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import android.os.AsyncTask;

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
 * @date 2015年8月4日 下午4:59:32
 *
 * @version 1.0.0
 */
public class AndroidConnection<T> extends Connection<T> {

	public AndroidConnection(AndroidClient client, String protocol, String format, String implementation, Class<T> i, LinkedHashSet<Interceptor> is, BeanFactory beanFactory, StreamFactory sf) throws Exception {
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
		@SuppressWarnings("unchecked")
		Callback<Object> callback = (Callback<Object>) arguments[arguments.length - 1];
		callback = callback == null ? Callback.DEFAULT : callback;
		try {
			new AndroidAsyncTask<Object, Integer, Object>(callback, new Block<Object, Object>() {

				public Object call(Object... parameters) throws Throwable {
					return AndroidConnection.super.invoke(proxy, method, parameters);
				}

			}).execute(Arrays.copyOf(arguments, arguments.length - 1));
		} catch (Throwable throwable) {
			callback.onCompleted(false, null, throwable);
		}
		return null;
	}

	private static class AndroidAsyncTask<Parameter, Progress, Result> extends AsyncTask<Parameter, Progress, Result> {
		private final Callback<Object> callback;
		private final Block<Parameter, Result> block;
		private Throwable throwable;

		public AndroidAsyncTask(Callback<Object> callback, Block<Parameter, Result> block) {
			super();
			this.callback = callback;
			this.block = block;
		}

		@Override
		protected Result doInBackground(Parameter... parameters) {
			try {
				return block.call(parameters);
			} catch (Throwable throwable) {
				this.throwable = throwable;
				return null;
			}
		}

		@Override
		protected void onPostExecute(Object result) {
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

	}

}
