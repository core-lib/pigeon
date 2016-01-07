package payne.framework.pigeon.core.encryption.extend;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.encoding.extend.Encode;
import payne.framework.pigeon.core.encryption.InvocationSigner;

/**
 * 数据签名,使用数据签名功能需要对框架进行和使用{@link Encrypt}一样的配置,可以通过实现{@link InvocationSigner} 对框架的签名算法进行拓展<br/>
 * 加上该注解的服务,服务端会用非对称私钥对数据进行签名,拼接在数据的后面并且用分隔符{@link Sign#separator()}分隔.<br/>
 * 注意.这里配置的编码方式{@link Sign#dataEncoding()} ,是对数据的编码,签名数据的编码采用大写的HEX编码方式.客户端收到数据时先读取到分隔符.解码便得到数据,<br/>
 * 剩下的数据解码就得到签名.用公钥验签通过才继续执行否则抛出异常.证明数据在传输过程中被篡改或损坏.
 * 
 * @see {@link Encrypt}
 * @see {@link InvocationSigner}
 * @see {@link InvocationSignProcedure}
 * 
 * @author yangchangpei
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Documented
@Inherited
@Process(procedure = InvocationSignProcedure.class, step = 3)
public @interface Sign {

	String value() default "SHA1WithRSA";

	/**
	 * 数据编码格式,因为数据签名是放在的后面,为了能够与数据区分开来,所以需要对数据编码并用分隔符拆分
	 * 
	 * @return 数据编码格式
	 */
	Encode dataEncoding() default @Encode("base64");

	/**
	 * 签名编码格式
	 * 
	 * @return 签名编码格式
	 */
	Encode signatureEncoding() default @Encode("hex");

	/**
	 * 分隔符,分隔符必须不在编码的字符集合里面,不然无法正确拆分
	 * 
	 * @return 分隔符
	 */
	byte separator() default '|';

}
