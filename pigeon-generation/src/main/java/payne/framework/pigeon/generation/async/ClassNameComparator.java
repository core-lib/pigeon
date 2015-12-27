package payne.framework.pigeon.generation.async;

import java.util.Comparator;

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
 * @date 2015年12月18日 下午8:35:09
 *
 * @version 1.0.0
 */
public class ClassNameComparator implements Comparator<Class<?>> {

	public int compare(Class<?> a, Class<?> b) {
		return a.getName().compareTo(b.getName());
	}

}
