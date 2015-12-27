package payne.framework.pigeon.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import payne.framework.pigeon.core.encoding.InvocationEncoder;
import payne.framework.pigeon.core.processing.InvocationEncodeProcedure;

/**
 * 将传输数据进行指定编码格式的编码,默认值是采用base64编码<br/>
 * 系统默认提供base64和base32格式的编码.使用者可以通过{@link InvocationEncoder}
 * 对编码方式进行拓展,并且通过配置文件集成到系统中,通常地配置方式为 编码算法缩写=实现类全称
 * 
 * @see {@link InvocationEncoder}
 * @see {@link InvocationEncodeProcedure}
 * 
 * @author ron
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Documented
@Inherited
@Process(procedure = InvocationEncodeProcedure.class, step = 5)
public @interface Encode {

	/**
	 * 编码格式,默认为:base64
	 * 
	 * @return 编码格式
	 */
	String value() default "base64";

}
