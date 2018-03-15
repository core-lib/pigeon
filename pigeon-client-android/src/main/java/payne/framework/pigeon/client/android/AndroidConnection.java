package payne.framework.pigeon.client.android;

import android.os.AsyncTask;
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
 * @date 2015年8月4日 下午4:59:32
 */
public class AndroidConnection<T> extends Connection<T> {

    public AndroidConnection(AndroidClient client, String implementation, Class<T> interfase) throws Exception {
        super(client, implementation, interfase);
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

        try {
            new AndroidAsyncTask<Object, Integer, Object>(callback, onSuccess, onFail, onCompleted, new Block<Object, Object>() {

                public Object call(Object... parameters) throws Throwable {
                    return AndroidConnection.super.invoke(proxy, method, parameters);
                }

            }).execute(args.toArray());
        } catch (Throwable throwable) {
            callback.onCompleted(false, null, throwable);
            onFail.onFail(throwable);
            onCompleted.onCompleted(false, null, throwable);
        }
        return null;
    }

    private static class AndroidAsyncTask<Parameter, Progress, Result> extends AsyncTask<Parameter, Progress, Result> {
        private final Callback callback;
        private final OnSuccess onSuccess;
        private final OnFail onFail;
        private final OnCompleted onCompleted;
        private final Block<Parameter, Result> block;
        private Throwable throwable;

        public AndroidAsyncTask(Callback callback, OnSuccess onSuccess, OnFail onFail, OnCompleted onCompleted, Block<Parameter, Result> block) {
            this.callback = callback;
            this.onSuccess = onSuccess;
            this.onFail = onFail;
            this.onCompleted = onCompleted;
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
