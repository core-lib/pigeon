package payne.framework.pigeon.core.processing;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.formatting.FormatInvocationInputStream;
import payne.framework.pigeon.core.formatting.FormatInvocationOutputStream;
import payne.framework.pigeon.core.formatting.InvocationFormatter;
import payne.framework.pigeon.core.formatting.Structure;
import payne.framework.pigeon.core.protocol.Channel;

public class InvocationFormatProcedure implements Procedure<Annotation> {
	private InvocationFormatter formatter;
	private Structure structure;

	public InvocationFormatProcedure(InvocationFormatter formatter) {
		super();
		this.formatter = formatter;
	}

	public InvocationFormatProcedure(InvocationFormatter formatter, Structure structure) {
		super();
		this.formatter = formatter;
		this.structure = structure;
	}

	public void initialize(int side, Process process, Annotation annotation, BeanFactory beanFactory, StreamFactory streamFactory, Header header, Channel channel) throws Exception {

	}

	public FormatInvocationOutputStream wrap(int side, Process process, Annotation annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, OutputStream outputStream) throws Exception {
		return new FormatInvocationOutputStream(formatter, outputStream, header.getCharset());
	}

	public FormatInvocationInputStream wrap(int side, Process process, Annotation annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, InputStream inputStream) throws Exception {
		return new FormatInvocationInputStream(formatter, structure, inputStream, header.getCharset());
	}

	public InvocationFormatter getFormatter() {
		return formatter;
	}

	public void setFormatter(InvocationFormatter formatter) {
		this.formatter = formatter;
	}

}
