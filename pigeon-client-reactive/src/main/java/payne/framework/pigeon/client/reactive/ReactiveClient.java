package payne.framework.pigeon.client.reactive;

import java.io.IOException;

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
 * @date 2015年8月5日 上午10:12:10
 *
 * @version 1.0.0
 */
public class ReactiveClient extends Client {

	public ReactiveClient(String host) {
		super(host);
	}

	public ReactiveClient(String host, int port) throws IOException {
		super(host, port);
	}

	@Override
	public <T> ReactiveConnection<T> build(String implementation, Class<T> interfase) throws Exception {
		if (!beanFactory.contains(protocol)) {
			throw new UnsupportedChannelException(protocol);
		}
		if (!beanFactory.contains(format)) {
			throw new UnsupportedFormatException(format);
		}
		ReactiveConnection<T> connection = new ReactiveConnection<T>(this, implementation, interfase);
		return connection;
	}

}
