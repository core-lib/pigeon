package payne.framework.pigeon.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import payne.framework.pigeon.core.digestion.InvocationDigester;
import payne.framework.pigeon.core.processing.InvocationValidateProcedure;

/**
 * 数据完整性校验,用摘要算法对数据计算摘要,然后对数据用指定编码{@link Validate#dataEncoding()}编码,摘要用大写HEX编码<br/>
 * 最后用分隔符{@link Validate#separator()}分隔拼接在一起.<br/>
 * 不像数据签名{@link Sign}服务端签名,客户端验签.数据摘要校验是双向的,即服务端和客户端双方都会相互校验<br/>
 * 如果校验不通过即证明数据被篡改或损坏将抛出异常.<br/>
 * 通过{@link InvocationDigester}对摘要算法进行拓展.
 * 
 * @see {@link InvocationDigester}
 * @see {@link InvocationValidateProcedure}
 * 
 * @author ron
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Documented
@Inherited
@Process(procedure = InvocationValidateProcedure.class, step = 4)
public @interface Validate {

	/**
	 * 校验方式,摘要算法
	 * 
	 * @return 摘要算法
	 */
	String value() default "MD5";

	/**
	 * 数据编码格式,因为数据摘要是放在的后面,为了能够与数据区分开来,所以需要对数据编码并用分隔符拆分
	 * 
	 * @return 数据编码格式
	 */
	Encode dataEncoding() default @Encode("base64");

	/**
	 * 数据摘要编码格式
	 * 
	 * @return 数据摘要编码格式
	 */
	Encode digestEncoding() default @Encode("hex");

	/**
	 * 分隔符,分隔符必须不在编码的字符集合里面,不然无法正确拆分
	 * 
	 * @return 分隔符
	 */
	byte separator() default '|';

}
