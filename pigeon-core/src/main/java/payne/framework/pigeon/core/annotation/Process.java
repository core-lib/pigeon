package payne.framework.pigeon.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import payne.framework.pigeon.core.processing.Procedure;

/**
 * 由于框架的一个主要思想就是通过注解对框架的功能进行拓展和配置,每个标注了该注解的注解对应一个工序.<br/>
 * 如 加密 签名 压缩 编码 校验 等等都是一个个工序,工序具体逻辑通过实现{@link Procedure}来实现.<br/>
 * 该注解用于注解上,用于标注框架需要将注解看作为数据的一道工序.
 * 
 * @see {@link Procedure}
 * 
 * @see {@link Encrypt}
 * @see {@link Compress}
 * @see {@link Sign}
 * @see {@link Encode}
 * @see {@link Validate}
 * @see {@link Work}
 * 
 * @author yangchangpei
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.ANNOTATION_TYPE })
@Documented
public @interface Process {

	/**
	 * 数据工序的实现类
	 * 
	 * @return
	 */
	Class<? extends Procedure<? extends Annotation>> procedure();

	/**
	 * 工序在第几步执行
	 * 
	 * @return
	 */
	float step() default 0;

}
