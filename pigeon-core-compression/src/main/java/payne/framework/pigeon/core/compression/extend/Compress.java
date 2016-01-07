package payne.framework.pigeon.core.compression.extend;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.compression.InvocationCompressor;

/**
 * 数据压缩,框架提供压缩解压接口,可以自定义适合系统的数据压缩方式,节省传输流量,和提高传输效率<br/>
 * 注解用于开放接口的上,并且指定压缩算法,默认采用GZIP压缩算法,使用者可以通过实现{@link InvocationCompressor}
 * 来对框架的压缩算法进行拓展,并且配置到配置文件中,通常情况下配置方式是:压缩算法的缩写=实现类全称
 * 
 * @see {@link InvocationCompressor}
 * @see {@link InvocationCompressProcedure}
 * 
 * @author ron
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Documented
@Inherited
@Process(procedure = InvocationCompressProcedure.class, step = 1)
public @interface Compress {

	String value() default "GZIP";

}
