package payne.framework.pigeon.client.async;

import payne.framework.pigeon.client.Connection;
import payne.framework.pigeon.client.exception.NonopenMethodException;
import payne.framework.pigeon.core.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
 * @date 2015年8月5日 上午10:15:11
 */
public class AsyncConnection<T> extends Connection<T> {

    public AsyncConnection(AsyncClient client, String implementation, Class<T> interfase) throws Exception {
        super(client, implementation, interfase);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        if (!Pigeons.isOpenableMethod(method)) {
            // 如果是 Object 的方法 那么可以直接调用不会通过网络进行远程调用
            if (method.getDeclaringClass() == Object.class) return method.invoke(this, arguments);
            throw new NonopenMethodException(interfase, method, arguments);
        }
        AsyncClient ac = (AsyncClient) client;
        ac.getExecutor().execute(new Execution(arguments, method));
        return null;
    }

    private class Execution implements Runnable {
        private final Object[] arguments;
        private final Method method;

        Execution(Object[] arguments, Method method) {
            this.arguments = arguments;
            this.method = method;
        }

        @Override
        public void run() {
            Callback callback = Callback.DEFAULT;
            OnSuccess onSuccess = OnSuccess.DEFAULT;
            OnFail onFail = OnFail.DEFAULT;
            OnCompleted onCompleted = OnCompleted.DEFAULT;

            List<Object> args = new ArrayList<Object>();
            for (Object arg : arguments) {
                if (arg instanceof Callback<?>) callback = (Callback<?>) arg;
                else if (arg instanceof OnSuccess<?>) onSuccess = (OnSuccess<?>) arg;
                else if (arg instanceof OnFail) onFail = (OnFail) arg;
                else if (arg instanceof OnCompleted) onCompleted = (OnCompleted) arg;
                else args.add(arg);
            }

            Object result = null;
            Throwable throwable = null;
            try {
                result = AsyncConnection.super.invoke(proxy, method, args.toArray());
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

            try {
                if (throwable == null) {
                    onSuccess.onSuccess(result);
                } else {
                    onFail.onFail(throwable);
                }
            } finally {
                onCompleted.onCompleted(throwable == null, result, throwable);
            }
        }
    }

}
