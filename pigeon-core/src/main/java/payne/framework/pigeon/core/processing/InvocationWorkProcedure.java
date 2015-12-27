package payne.framework.pigeon.core.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import payne.framework.pigeon.core.Constants;
import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.annotation.Work;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.protocol.Channel;

public class InvocationWorkProcedure implements Procedure<Work>, Constants {
	private final static Timer TIMER = new Timer(true);

	public void initialize(int side, Process process, Work annotation, BeanFactory beanFactory, StreamFactory streamFactory, Header header, Channel channel) throws Exception {
		Thread.currentThread().setPriority(annotation.priority());
		final Channel ch = channel;
		TIMER.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					ch.timeout();
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					this.cancel();
					TIMER.purge();
				}
			}

		}, annotation.timeout());
	}

	public OutputStream wrap(int side, Process process, Work annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, OutputStream outputStream) throws Exception {
		return outputStream;
	}

	public InputStream wrap(int side, Process process, Work annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, InputStream inputStream) throws Exception {
		return inputStream;
	}

}
