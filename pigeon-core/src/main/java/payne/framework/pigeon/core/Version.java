package payne.framework.pigeon.core;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @date 2015年12月24日 上午10:05:35
 *
 * @version 1.0.0
 */
public class Version {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static Version current;

	private final String name;
	private final String code;

	private Version() {
		URL url = Thread.currentThread().getContextClassLoader().getResource("pigeon-version.properties");
		Properties properties = new Properties();
		try {
			properties.load(url.openStream());
		} catch (IOException e) {
			logger.warn("read current version properties failed on : " + e.getMessage());
		}
		this.name = properties.getProperty("name", "pigeon");
		this.code = properties.getProperty("code", "1.0");
	}

	public static Version getCurrent() {
		if (current != null) {
			return current;
		}
		synchronized (Version.class) {
			if (current != null) {
				return current;
			}
			current = new Version();
		}
		return current;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

}
