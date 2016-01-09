package payne.framework.pigeon.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import payne.framework.pigeon.core.checking.Checker;
import payne.framework.pigeon.core.exception.IllegalConfigException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;

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

	public static enum Mode implements Checker {
		GET(false) {

			public boolean check(BeanFactory beanFactory, Object implementation, Class<?> interfase, Method method) throws IllegalConfigException {
				return true;
			}

		},
		HEAD(false) {

			public boolean check(BeanFactory beanFactory, Object implementation, Class<?> interfase, Method method) throws IllegalConfigException {
				// TODO Auto-generated method stub
				return false;
			}

		},
		POST(true) {

			public boolean check(BeanFactory beanFactory, Object implementation, Class<?> interfase, Method method) throws IllegalConfigException {
				return true;
			}

		},
		PUT(true) {

			public boolean check(BeanFactory beanFactory, Object implementation, Class<?> interfase, Method method) throws IllegalConfigException {
				// TODO Auto-generated method stub
				return false;
			}

		},
		PATCH(true) {

			public boolean check(BeanFactory beanFactory, Object implementation, Class<?> interfase, Method method) throws IllegalConfigException {
				// TODO Auto-generated method stub
				return false;
			}

		},
		DELETE(false) {

			public boolean check(BeanFactory beanFactory, Object implementation, Class<?> interfase, Method method) throws IllegalConfigException {
				// TODO Auto-generated method stub
				return false;
			}

		},
		OPTIONS(false) {

			public boolean check(BeanFactory beanFactory, Object implementation, Class<?> interfase, Method method) throws IllegalConfigException {
				// TODO Auto-generated method stub
				return false;
			}

		},
		TRACE(false) {

			public boolean check(BeanFactory beanFactory, Object implementation, Class<?> interfase, Method method) throws IllegalConfigException {
				// TODO Auto-generated method stub
				return false;
			}

		};

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
