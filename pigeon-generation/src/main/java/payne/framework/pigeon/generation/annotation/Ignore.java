package payne.framework.pigeon.generation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 生成的时候可以通过该注解忽略指定的属性,当用于类型上可以通过properties指定忽略的属性,或者在字段或getter方法上也可以忽略指定的属性,
 * 而且三种方式的忽略属性同时起作用
 * 
 * @author ron
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Documented
@Inherited
public @interface Ignore {

	/**
	 * 此属性只当注解用于类型上才起效,可以同时指定多个忽略的属性名字
	 * 
	 * @return 忽略的属性名数组
	 */
	String[] properties() default {};

}
