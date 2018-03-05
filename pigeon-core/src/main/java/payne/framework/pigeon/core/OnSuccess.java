package payne.framework.pigeon.core;

/**
 * 成功回调
 *
 * @author 杨昌沛 646742615@qq.com
 * @company 丽晶软件
 * @project 牛厂
 * @date 2018-03-05 19:13
 **/
public interface OnSuccess<R> {

    OnSuccess<Object> DEFAULT = new OnSuccess<Object>() {
        @Override
        public void onSuccess(Object result) {

        }
    };

    void onSuccess(R result);

}
