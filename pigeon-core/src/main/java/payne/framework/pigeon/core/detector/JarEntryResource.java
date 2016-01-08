package payne.framework.pigeon.core.detector;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
 * @date 2016年1月8日 下午3:08:07
 *
 * @version 1.0.0
 */
public class JarEntryResource extends Resource {
	private final JarFile jarFile;
	private final JarEntry jarEntry;

	public JarEntryResource(JarFile jarFile, JarEntry jarEntry) {
		super(jarEntry.getName());
		this.jarFile = jarFile;
		this.jarEntry = jarEntry;
	}

	public InputStream getInputStream() throws IOException {
		return jarFile.getInputStream(jarEntry);
	}

}
