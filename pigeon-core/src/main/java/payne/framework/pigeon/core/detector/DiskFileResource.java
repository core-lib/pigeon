package payne.framework.pigeon.core.detector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
 * @date 2016年1月8日 下午3:07:06
 *
 * @version 1.0.0
 */
public class DiskFileResource extends Resource {
	private final File file;

	public DiskFileResource(File file) {
		super(file.getName());
		this.file = file;
	}

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

}
