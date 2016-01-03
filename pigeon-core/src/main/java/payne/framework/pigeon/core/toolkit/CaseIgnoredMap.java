package payne.framework.pigeon.core.toolkit;

import java.util.LinkedHashMap;
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
 * @date 2016年1月3日 下午5:30:28
 *
 * @version 1.0.0
 */
public class CaseIgnoredMap<V> extends LinkedHashMap<String, V> {
	private static final long serialVersionUID = 370220303736956513L;

	public CaseIgnoredMap() {
		super();
	}

	public CaseIgnoredMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	public CaseIgnoredMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public CaseIgnoredMap(int initialCapacity) {
		super(initialCapacity);
	}

	public CaseIgnoredMap(Map<? extends String, ? extends V> m) {
		super(m);
	}

	@Override
	public V get(Object key) {
		if (key == null || key instanceof String == false) {
			return super.get(key);
		}
		for (java.util.Map.Entry<String, V> entry : this.entrySet()) {
			if (key.toString().equalsIgnoreCase(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}

}
