package payne.framework.pigeon.client.async;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class AsyncClient extends Client {
	private ExecutorService executor = Executors.newFixedThreadPool(10);

	public AsyncClient(String host) {
		super(host);
	}

	public AsyncClient(String host, int port) throws IOException {
		super(host, port);
	}

	@Override
	public <T> AsyncConnection<T> build(String implementation, Class<T> interfase) throws Exception {
		if (!beanFactory.contains(protocol)) {
			throw new UnsupportedChannelException(protocol);
		}
		if (!beanFactory.contains(format)) {
			throw new UnsupportedFormatException(format);
		}
		AsyncConnection<T> connection = new AsyncConnection<T>(this, implementation, interfase);
		return connection;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		if (executor == null || executor.isShutdown()) {
			throw new IllegalArgumentException("unable to set a null or shutdown executor");
		}
		if (this.executor != null) {
			List<Runnable> runnables = this.executor.shutdownNow();
			for (Runnable runnable : runnables) {
				executor.submit(runnable);
			}
		}
		this.executor = executor;
	}

}
