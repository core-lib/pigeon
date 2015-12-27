package payne.framework.pigeon.core.processing;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.protocol.Channel;

public interface Procedure<T> {

	void initialize(int side, Process process, T annotation, BeanFactory beanFactory, StreamFactory streamFactory, Header header, Channel channel) throws Exception;

	OutputStream wrap(int side, Process process, T annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, OutputStream outputStream) throws Exception;

	InputStream wrap(int side, Process process, T annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, InputStream inputStream) throws Exception;

}
