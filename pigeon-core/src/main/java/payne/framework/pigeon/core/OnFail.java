package payne.framework.pigeon.core;

/**
 * 失败回调
 *
 * @author 杨昌沛 646742615@qq.com
 * @company 丽晶软件
 * @project 牛厂
 * @date 2018-03-05 19:14
 **/
public interface OnFail {

    OnFail DEFAULT = new OnFail() {
        @Override
        public void onFail(Throwable throwable) {

        }
    };

    void onFail(Throwable throwable);

}
