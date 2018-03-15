package payne.framework.pigeon.client.reactive;

import payne.framework.pigeon.client.Connection;
import payne.framework.pigeon.client.exception.NonopenMethodException;
import payne.framework.pigeon.core.Pigeons;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.lang.reflect.Method;

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
public class ReactiveConnection<T> extends Connection<T> {

    public ReactiveConnection(ReactiveClient client, String implementation, Class<T> interfase) throws Exception {
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
        return Observable
                .create(new Observable.OnSubscribe<Object>() {
                    public void call(Subscriber<Object> subscriber) {
                        try {
                            Object result = ReactiveConnection.super.invoke(proxy, method, arguments);
                            subscriber.onNext(result);
                        } catch (Throwable e) {
                            subscriber.onError(e);
                        } finally {
                            subscriber.onCompleted();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
