package payne.framework.pigeon.core.detector;

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
 * @date 2016年1月8日 上午11:43:17
 *
 * @version 1.0.0
 */
public class Resource {
	private String name;
	private StreamOpener opener;

	public Resource(String name, StreamOpener opener) {
		super();
		this.name = name;
		this.opener = opener;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InputStream openStream() throws IOException {
		return opener.open();
	}

	@Override
	public String toString() {
		return "Resource [name=" + name + "]";
	}

}
