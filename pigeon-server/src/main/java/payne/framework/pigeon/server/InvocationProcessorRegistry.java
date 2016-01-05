package payne.framework.pigeon.server;

import java.util.Set;
import java.util.regex.Pattern;

import payne.framework.pigeon.core.annotation.Accept.Mode;
import payne.framework.pigeon.core.exception.UnmappedPathException;
import payne.framework.pigeon.server.InvocationProcessorRegistry.Registration;
import payne.framework.pigeon.server.exception.UnregulatedInterfaceException;

/**
 * 远程调用处理器登记,用于管理容器里面远程访问路径与处理器
 * 
 * @author ron
 * 
 */
public interface InvocationProcessorRegistry extends Iterable<Registration> {

	/**
	 * 获取路径集合
	 * 
	 * @return 路径集合
	 */
	Set<Path> paths();

	/**
	 * 获取处理器集合
	 * 
	 * @return 处理器集合
	 */
	Set<InvocationProcessor> processors();

	/**
	 * 查询路径匹配指定正则表达式的路径-处理器对
	 * 
	 * @param regex
	 *            正则表达式
	 * @return 路径匹配指定正则表达式的路径-处理器对
	 */
	Set<Registration> matches(String regex);

	/**
	 * 测试路径是否存在
	 * 
	 * @param mode
	 *            请求方法
	 * @param path
	 *            路径
	 * @return 如果存在:true 否则:false
	 */
	boolean exists(Mode mode, String path);

	/**
	 * 通过指定的路径寻找对应的处理器
	 * 
	 * @param mode
	 *            请求方法
	 * @param path
	 *            路径
	 * @return 对应处理器
	 * @throws UnmappedPathException
	 *             路径不存在时抛出的异常
	 */
	InvocationProcessor lookup(Mode mode, String path) throws UnmappedPathException;

	/**
	 * 注册开放接口实现类
	 * 
	 * @param service
	 *            开放接口实现类对象
	 * @throws UnregulatedInterfaceException
	 *             不规范的开放接口异常
	 */
	void register(Object service) throws UnregulatedInterfaceException;

	/**
	 * 撤销开放接口实现类的登记
	 * 
	 * @param service
	 *            开放接口实现类对象
	 * @throws IllegalArgumentException
	 *             如果参数不是一个开放接口实现类抛出IllegalArgumentException
	 */
	void revoke(Object service) throws IllegalArgumentException;

	/**
	 * 获取注册的所有服务
	 * 
	 * @return 注册的所有服务
	 */
	Set<Object> services();

	/**
	 * 清楚所有登记记录
	 */
	void clear();

	/**
	 * 路径-处理器对
	 * 
	 * @author yangchangpei
	 * 
	 */
	public static class Registration {
		private final Mode mode;
		private final Pattern pattern;
		private final String expression;
		private final InvocationProcessor processor;

		public Registration(Mode mode, Pattern pattern, InvocationProcessor processor) {
			super();
			this.mode = mode;
			this.pattern = pattern;
			this.expression = pattern.pattern();
			this.processor = processor;
		}

		public Mode getMode() {
			return mode;
		}

		public String getExpression() {
			return expression;
		}

		public Pattern getPattern() {
			return pattern;
		}

		public InvocationProcessor getProcessor() {
			return processor;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((expression == null) ? 0 : expression.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Registration other = (Registration) obj;
			if (expression == null) {
				if (other.expression != null)
					return false;
			} else if (!expression.equals(other.expression))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "{" + mode + " " + expression + " : " + processor + "}";
		}
	}

}
