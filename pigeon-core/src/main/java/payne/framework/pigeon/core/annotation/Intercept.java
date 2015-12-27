package payne.framework.pigeon.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import payne.framework.pigeon.core.Interceptor;

/**
 * 开放接口实现类的请求拦截器组,用于定义该开放接口实现类的开放方法过滤拦截功能;不能用于接口上,否则失效
 * 
 * @author Payne
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
@Documented
@Inherited
public @interface Intercept {

	/**
	 * 拦截器类数组,不能有相同的拦截器类,同时必须考虑拦截器数组的顺序
	 * 
	 * @return 拦截器类数组
	 */
	Class<? extends Interceptor>[] value();

	/**
	 * 是否继承父类的拦截器,默认为true
	 * 
	 * @return true:继承 false:不继承
	 */
	boolean inherit() default true;

}
