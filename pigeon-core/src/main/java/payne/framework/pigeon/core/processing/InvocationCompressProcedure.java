package payne.framework.pigeon.core.processing;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.annotation.Compress;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.compression.InvocationCompressor;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.protocol.Channel;

public class InvocationCompressProcedure implements Procedure<Compress> {

	public void initialize(int side, Process process, Compress annotation, BeanFactory beanFactory, StreamFactory streamFactory, Header header, Channel channel) throws Exception {

	}

	public OutputStream wrap(int side, Process process, Compress annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, OutputStream outputStream) throws Exception {
		InvocationCompressor compressor = beanFactory.get(annotation.value(), InvocationCompressor.class);
		return compressor.wrap(outputStream);
	}

	public InputStream wrap(int side, Process process, Compress annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, InputStream inputStream) throws Exception {
		InvocationCompressor compressor = beanFactory.get(annotation.value(), InvocationCompressor.class);
		return compressor.wrap(inputStream);
	}

}
