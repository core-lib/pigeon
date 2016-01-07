package payne.framework.pigeon.core.digestion.extend;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.digestion.InvocationDigester;
import payne.framework.pigeon.core.encoding.InvocationEncoder;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.processing.Procedure;
import payne.framework.pigeon.core.protocol.Channel;

public class InvocationValidateProcedure implements Procedure<Validate> {

	public void initialize(int side, Process process, Validate annotation, BeanFactory beanFactory, StreamFactory streamFactory, Header header, Channel channel) throws Exception {

	}

	public OutputStream wrap(int side, Process process, Validate annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, OutputStream outputStream) throws Exception {
		InvocationDigester digester = beanFactory.get(annotation.value(), InvocationDigester.class);
		InvocationEncoder dataEncoder = beanFactory.get(annotation.dataEncoding().value(), InvocationEncoder.class);
		InvocationEncoder digestEncoder = beanFactory.get(annotation.digestEncoding().value(), InvocationEncoder.class);
		return digester.wrap(outputStream, dataEncoder, digestEncoder, annotation.separator());
	}

	public InputStream wrap(int side, Process process, Validate annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, InputStream inputStream) throws Exception {
		InvocationDigester digester = beanFactory.get(annotation.value(), InvocationDigester.class);
		InvocationEncoder dataEncoder = beanFactory.get(annotation.dataEncoding().value(), InvocationEncoder.class);
		InvocationEncoder digestEncoder = beanFactory.get(annotation.digestEncoding().value(), InvocationEncoder.class);
		return digester.wrap(inputStream, dataEncoder, digestEncoder, annotation.separator());
	}

}
