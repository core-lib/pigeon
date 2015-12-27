package payne.framework.pigeon.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 开放接口或方法,用于定义需要开放的接口和方法
 * 
 * @author Payne
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Documented
@Inherited
public @interface Open {
	
	/**
	 * 访问路径,当注解用于接口上默认情况下该值等于接口的简单名称,当注解用于方法上默认值等于方法名称<br/>
	 * 框架会自动用"/"对各个层级的路径进行分隔,所以前面加不加"/"都可以
	 * 
	 * @return 访问路径
	 */
	String value() default "";

	/**
	 * 是否接口的所有方法都开放,所以该属性只对注解用于接口上有效,默认值是 true<br/>
	 * 1.当该值为true的时候,所有的方法,标注了 {@link Close}除外都会开放远程访问
	 * 2.当该值为false的时候,只有标注了该注解的方法才会开放<br/>
	 * 
	 * @return 是否开放所有方法
	 */
	boolean all() default true;

	/**
	 * 配置客户端连接的worker线程参数
	 * 
	 * @return
	 */
	Work work() default @Work;

}
