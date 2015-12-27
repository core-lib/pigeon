package payne.framework.pigeon.core;

import java.lang.annotation.Annotation;
import java.util.Comparator;

import payne.framework.pigeon.core.annotation.Process;

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
 * @date 2015年12月21日 下午2:50:07
 *
 * @version 1.0.0
 */
public class ProcessComparator implements Comparator<Class<? extends Annotation>> {
	
	public int compare(Class<? extends Annotation> a, Class<? extends Annotation> b) {
		return Float.valueOf(a.getAnnotation(Process.class).step()).compareTo(Float.valueOf(b.getAnnotation(Process.class).step()));
	}

}
