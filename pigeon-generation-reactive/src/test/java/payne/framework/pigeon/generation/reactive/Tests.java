package payne.framework.pigeon.generation.reactive;

import org.junit.Test;
import payne.framework.pigeon.generation.Generator;
import payne.framework.pigeon.generation.Interface;

import java.io.File;

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
 * @date 2016年1月8日 下午9:08:23
 *
 * @version 1.0.0
 */
public class Tests {

	@Test
	public void testGenerate() throws Exception {
		File directory = new File(System.getProperty("java.io.tmpdir") + "reactive");
		if (!directory.exists() && !directory.mkdirs()) throw new IllegalStateException("创建文件夹失败");
		Generator generator = new ReactiveGenerator(directory);
		generator.generate(new Interface("/", SampleAPI.class));
		System.out.println(directory);
	}
	
}
