package payne.framework.pigeon.core.encryption.extend;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.encoding.extend.Encode;
import payne.framework.pigeon.core.encryption.InvocationEncryptor;

/**
 * 加密,注解用于接口或接口的抽象方法上,用于标注服务需要对传输的数据加密,通过{@link Encrypt#algorithm()}<br/>
 * 指定加密算法(通常是对称的),加密算法可以通过实现{@link InvocationEncryptor}来拓展.<br/>
 * 使用{@link Encrypt#keysize()}指定密钥长度,<br/>
 * 1.使用加密功能需要对框架进行非对称密钥的配置,预先生成一对非对称密钥(如RSA),将私钥配置在服务端和将公钥配置在客户端.<br/>
 * 1.1.服务端配置方式: <br/>
 * private-key-algorithm=非对称加密算法(如RSA)<br/>
 * private-key-encoding=私钥编码方式(如base64) <br/>
 * private-key-size=私钥大小(如1024)<br/>
 * private-key-encoded=编码后的私钥 <br/>
 * <br/>
 * 1.2.客户端配置方式: <br/>
 * public-key-algorithm=非对称加密算法(如RSA)<br/>
 * public-key-encoding=公钥编码方式(如base64) <br/>
 * public-key-size=公钥大小(如1024)<br/>
 * public-key-encoded=编码后的公钥<br/>
 * <br/>
 * 2.框架对每一次请求都会生成指定加密算法和长度的对称密钥和初始化向量.<br/>
 * 3.框架会自动将生成的对称密钥和初始化向量用非对称密钥加密在用{@link Encrypt#keyEncoding()}编码<br/>
 * 4.用生成的对称密钥和向量对明文进行加密 <br/>
 * 5.加密后的数据格式为 [编码后的非对称公钥加密过的对称密钥][分隔符][编码后的非对称公钥加密过的初始化向量][分隔符][密文]<br/>
 * 6.服务端接收到数据,先读取到第一个分隔符,解码解密得到对称密钥,读到第二个分隔符解码解密得到初始化向量,用对称密钥和初始化向量对后面的密文进行解密得到<br/>
 * 7.服务端直接用对称密钥和初始化向量对回应数据加密发送给客户端.<br/>
 * 8.客户端直接解密<br/>
 * 因此,分隔符必须不在编码的字符集里面.否则可能无法解密.<br/>
 * 
 * @see {@link InvocationEncryptor}
 * @see {@link InvocationEncryptProcedure}
 * 
 * @author ron
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Documented
@Inherited
@Process(procedure = InvocationEncryptProcedure.class, step = 2)
public @interface Encrypt {

	/**
	 * 数据传输使用的加密算法(一般为对称加密)
	 * 
	 * @return 加密算法
	 */
	String algorithm() default "DES";

	/**
	 * 密钥大小
	 * 
	 * @return 密钥大小
	 */
	int keysize() default 56;

	/**
	 * 密钥的编码格式,因为客户端生成的密钥需要用服务端公钥加密然后放在数据的前面一起发送给服务端,<br/>
	 * 所以需要有依据将密钥/向量/数据三者拆分开来,所以需要编码和分隔符,为了性能考虑只会对密钥和向量编码,数据本身不编码
	 * 
	 * @return 密钥的编码格式
	 */
	Encode keyEncoding() default @Encode("base64");

	/**
	 * 向量的编码格式,因为客户端生成的向量需要用服务端公钥加密然后放在数据的前面一起发送给服务端,<br/>
	 * 所以需要有依据将密钥/向量/数据三者拆分开来,所以需要编码和分隔符,为了性能考虑只会对密钥和向量编码,数据本身不编码
	 * 
	 * @return 向量的编码格式
	 */
	Encode ivEncoding() default @Encode("base64");

	/**
	 * 密钥/向量/数据的分隔符,分隔符必须不在编码的字符集合里面,不然无法正确拆分
	 * 
	 * @return 密钥/向量/数据的分隔符
	 */
	byte separator() default '|';

}
