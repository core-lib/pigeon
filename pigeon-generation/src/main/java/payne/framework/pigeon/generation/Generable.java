package payne.framework.pigeon.generation;

/**
 * 可生成的
 *
 * @Author 杨昌沛
 */
public interface Generable extends Named {

    /**
     * 获取可生成对象的Java类型
     *
     * @return java 类型
     */
    Class<?> getType();

}
