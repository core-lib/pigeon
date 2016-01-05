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
		GET(false), HEAD(false), POST(true), PUT(true), PATCH(true), DELETE(false), OPTIONS(false), TRACE(false);

		public final boolean bodied;

		private Mode(boolean bodied) {
			this.bodied = bodied;
		}

		public static Mode likeOf(String name) {
			if (name == null) {
				return null;
			}
			for (Mode mode : values()) {
				if (mode.name().equalsIgnoreCase(name.trim())) {
					return mode;
				}
			}
			return null;
		}

	}

}
