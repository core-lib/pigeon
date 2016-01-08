package payne.framework.pigeon.server;

import java.util.concurrent.ExecutorService;

import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.observation.NotificationCenter;

public interface ConfigurableInvocationContext {

	int getConcurrent();

	void setConcurrent(int concurrent);

	ExecutorService getExecutor();

	void setExecutor(ExecutorService executor);

	BeanFactory getBeanFactory();

	void setBeanFactory(BeanFactory beanFactory);

	StreamFactory getStreamFactory();

	void setStreamFactory(StreamFactory streamFactory);

	InvocationProcessorRegistry getInvocationProcessorRegistry();

	void setInvocationProcessorRegistry(InvocationProcessorRegistry invocationProcessorRegistry);

	int getPriority();

	void setPriority(int priority);

	NotificationCenter getNotificationCenter();

	void setNotificationCenter(NotificationCenter notificationCenter);

}