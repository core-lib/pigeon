package payne.framework.pigeon.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author yangchangpei 646742615@qq.com
 *
 * @date 2016年1月5日 上午10:46:31
 *
 * @version 1.0.0
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Documented
@Inherited
public @interface Accept {

	public Mode[] modes() default {};

	public String[] media() default {};

	public static enum Mode {
		GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

		public static Mode likeOf(String name) {
			for (Mode mode : values()) {
				if (mode.name().equalsIgnoreCase(name)) {
					return mode;
				}
			}
			return null;
		}

	}

}
