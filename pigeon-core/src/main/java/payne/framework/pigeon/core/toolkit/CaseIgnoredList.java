package payne.framework.pigeon.core.toolkit;

import java.util.ArrayList;
import java.util.Collection;

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
 * @date 2016年1月5日 上午11:17:17
 *
 * @version 1.0.0
 */
public class CaseIgnoredList extends ArrayList<String> {
	private static final long serialVersionUID = -3269108922079642829L;

	public CaseIgnoredList() {
		super();
	}

	public CaseIgnoredList(Collection<? extends String> c) {
		super(c);
	}

	public CaseIgnoredList(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}

	@Override
	public int indexOf(Object o) {
		if (o == null || o instanceof String == false) {
			return super.indexOf(o);
		}
		for (int i = 0; i < size(); i++) {
			Object e = get(i);
			if (e != null && o.toString().equalsIgnoreCase(e.toString())) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object e : c) {
			if (contains(e) == false) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (contains(o)) {
			int index = indexOf(o);
			super.remove(index);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			changed = changed || remove(o);
		}
		return changed;
	}

}
