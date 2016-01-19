package payne.framework.pigeon.core.factory.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.qfox.detector.Resource;

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
 * @date 2016年1月8日 上午9:54:44
 *
 * @version 1.0.0
 */
public class Configuration implements Serializable {
	private static final long serialVersionUID = -3157552739199405768L;

	final String name;
	final Properties properties;

	public Configuration(Resource resource) throws IOException {
		this.name = resource.getName();
		this.properties = new Properties();
		InputStream inputStream = null;
		try {
			this.properties.load(inputStream = resource.getInputStream());
		} finally {
			inputStream.close();
		}
	}

	public Configuration(String name, Properties properties) {
		super();
		this.name = name;
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public Properties getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		return "Configuration [name=" + name + ", properties=" + properties + "]";
	}

}
