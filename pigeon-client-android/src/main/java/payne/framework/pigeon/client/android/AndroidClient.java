package payne.framework.pigeon.client.android;

import payne.framework.pigeon.client.Client;
import payne.framework.pigeon.core.exception.UnsupportedChannelException;
import payne.framework.pigeon.core.exception.UnsupportedFormatException;

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
 * @date 2015年8月4日 上午10:15:09
 *
 * @version 1.0.0
 */
public class AndroidClient extends Client {

	public AndroidClient(String host) {
		super(host);
	}

	public AndroidClient(String host, int port) {
		super(host, port);
	}

	@Override
	public <T> AndroidConnection<T> build(String implementation, Class<T> interfase) throws Exception {
		if (!beanFactory.contains(protocol)) {
			throw new UnsupportedChannelException(protocol);
		}
		if (!beanFactory.contains(format)) {
			throw new UnsupportedFormatException(format);
		}
		AndroidConnection<T> connection = new AndroidConnection<T>(this, implementation, interfase);
		return connection;
	}

}
