package payne.framework.pigeon.generation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

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
 * @date 2015年10月10日 下午7:14:11
 *
 * @version 1.0.0
 */
public class Result {
	private final Type type;
	private final boolean array;
	private final Type component;
	private final boolean mapping;

	public Result(Type type) {
		this.type = type;
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			if (clazz.isArray()) {
				this.array = true;
				this.component = clazz.getComponentType();
				this.mapping = false;
				return;
			}
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Class<?> clazz = (Class<?>) parameterizedType.getRawType();
			if (Collection.class.isAssignableFrom(clazz)) {
				this.array = true;
				this.component = parameterizedType.getActualTypeArguments()[0];
				this.mapping = false;
				return;
			} else if (Map.class.isAssignableFrom(clazz)) {
				this.array = false;
				this.component = parameterizedType.getActualTypeArguments()[1];
				this.mapping = true;
				return;
			}
		}
		this.array = false;
		this.component = null;
		this.mapping = false;
	}

	public Type getType() {
		return type;
	}

	public boolean isArray() {
		return array;
	}

	public Type getComponent() {
		return component;
	}

	public boolean isMapping() {
		return mapping;
	}

}
