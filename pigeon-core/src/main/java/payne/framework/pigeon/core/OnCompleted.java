package payne.framework.pigeon.core;

/**
 * 完成回调
 *
 * @author 杨昌沛 646742615@qq.com
 * @company 丽晶软件
 * @project 牛厂
 * @date 2018-03-05 19:15
 **/
public interface OnCompleted<R> {

    OnCompleted<Object> DEFAULT = new OnCompleted<Object>() {
        @Override
        public void onCompleted(boolean success, Object result, Throwable throwable) {

        }
    };

    void onCompleted(boolean success, R result, Throwable throwable);

}
