package payne.framework.pigeon.core.processing;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.annotation.Encode;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.encoding.InvocationEncoder;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.protocol.Channel;

public class InvocationEncodeProcedure implements Procedure<Encode> {

	public void initialize(int side, Process process, Encode annotation, BeanFactory beanFactory, StreamFactory streamFactory, Header header, Channel channel) throws Exception {

	}

	public OutputStream wrap(int side, Process process, Encode annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, OutputStream outputStream) throws Exception {
		InvocationEncoder encoder = beanFactory.get(annotation.value(), InvocationEncoder.class);
		return encoder.wrap(outputStream);
	}

	public InputStream wrap(int side, Process process, Encode annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, InputStream inputStream) throws Exception {
		InvocationEncoder encoder = beanFactory.get(annotation.value(), InvocationEncoder.class);
		return encoder.wrap(inputStream);
	}

}
