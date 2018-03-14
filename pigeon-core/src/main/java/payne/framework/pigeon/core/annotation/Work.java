package payne.framework.pigeon.core.annotation;

import payne.framework.pigeon.core.processing.InvocationWorkProcedure;

import java.lang.annotation.*;

/**
 * work线程的配置,用于配置服务的超时时长和运行线程优先级.
 * 
 * @see {@link InvocationWorkProcedure}
 * 
 * @author yangchangpei
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Documented
@Inherited
@Process(procedure = InvocationWorkProcedure.class, step = 0)
public @interface Work {

	/**
	 * work线程的优先级，值范围：[1,10]，默认值为：5，用于配置invocation的业务执行线程的优先级，但不能依赖此属性控制并发时执行的顺序
	 * 
	 * @return work线程的优先级
	 */
	int priority() default Thread.NORM_PRIORITY;

	/**
     * 超时时长,单位:毫秒 默认值:60 * 1000
     *
	 * @return
	 */
    int timeout() default 60 * 1000;

}
