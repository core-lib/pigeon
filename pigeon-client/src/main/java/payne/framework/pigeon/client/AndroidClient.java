package payne.framework.pigeon.client;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Properties;

import payne.framework.pigeon.core.Interceptor;
import payne.framework.pigeon.core.exception.UnsupportedChannelException;
import payne.framework.pigeon.core.exception.UnsupportedFormatException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;

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

	public AndroidClient(String host, int port, BeanFactory beanFactory, StreamFactory streamFactory) {
		super(host, port, beanFactory, streamFactory);
	}

	public AndroidClient(String host, int port, BeanFactory beanFactory) {
		super(host, port, beanFactory);
	}

	public AndroidClient(String host, int port, ClassLoader classLoader, BeanFactory beanFactory, StreamFactory streamFactory) {
		super(host, port, classLoader, beanFactory, streamFactory);
	}

	public AndroidClient(String host, int port, int timeout, BeanFactory beanFactory, StreamFactory streamFactory) {
		super(host, port, timeout, beanFactory, streamFactory);
	}

	public AndroidClient(String host, int port, int timeout, BeanFactory beanFactory) {
		super(host, port, timeout, beanFactory);
	}

	public AndroidClient(String host, int port, int timeout, ClassLoader classLoader, BeanFactory beanFactory, StreamFactory streamFactory) {
		super(host, port, timeout, classLoader, beanFactory, streamFactory);
	}

	public AndroidClient(String host, int port, int timeout, Properties properties) {
		super(host, port, timeout, properties);
	}

	public AndroidClient(String host, int port, int timeout, StreamFactory streamFactory) throws IOException {
		super(host, port, timeout, streamFactory);
	}

	public AndroidClient(String host, int port, int timeout, String... beanConfigurationPaths) throws IOException {
		super(host, port, timeout, beanConfigurationPaths);
	}

	public AndroidClient(String host, int port, int timeout) throws IOException {
		super(host, port, timeout);
	}

	public AndroidClient(String host, int port, Properties properties) {
		super(host, port, properties);
	}

	public AndroidClient(String host, int port, StreamFactory streamFactory) throws IOException {
		super(host, port, streamFactory);
	}

	public AndroidClient(String host, int port, String... beanConfigurationPaths) throws IOException {
		super(host, port, beanConfigurationPaths);
	}

	public AndroidClient(String host, int port) throws IOException {
		super(host, port);
	}

	@Override
	public <T> AndroidConnection<T> build(String protocol, String format, String implementation, Class<T> interfase, LinkedHashSet<Interceptor> interceptors) throws Exception {
		if (!beanFactory.contains(protocol)) {
			throw new UnsupportedChannelException(protocol);
		}

		if (!beanFactory.contains(format)) {
			throw new UnsupportedFormatException(format);
		}

		AndroidConnection<T> connection = new AndroidConnection<T>(this, protocol, format, implementation, interfase, new LinkedHashSet<Interceptor>(interceptors), beanFactory, streamFactory);

		return connection;
	}

}
