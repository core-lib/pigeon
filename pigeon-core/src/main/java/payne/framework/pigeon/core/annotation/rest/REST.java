package payne.framework.pigeon.core.annotation.rest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Documented
@Inherited
public @interface REST {

	String value();

	Method[] accept() default { Method.GET };

	public static enum Method {
		GET, POST, PUT, DELETE, OPTIONS, HEAD, TRACE;
	}

}
