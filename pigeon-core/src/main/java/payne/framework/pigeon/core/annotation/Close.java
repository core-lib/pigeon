package payne.framework.pigeon.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 封闭,用于开放接口的不希望被远程访问的方法上,标注了该注解的方法将不能被远程访问, 当该注解和{@link Open}
 * 注解同时作用于方法时,方法依然是封闭的.所以当方法上标注了该注解那么该方法一定是封闭的.
 * 
 * @see {@link Open}
 * 
 * @author Payne
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD })
@Documented
@Inherited
public @interface Close {

}
