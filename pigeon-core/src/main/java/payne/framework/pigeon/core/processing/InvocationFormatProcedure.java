package payne.framework.pigeon.core.processing;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.formatting.FormatInvocationInputStream;
import payne.framework.pigeon.core.formatting.FormatInvocationOutputStream;
import payne.framework.pigeon.core.formatting.InvocationFormatter;
import payne.framework.pigeon.core.protocol.Channel;

public class InvocationFormatProcedure implements Procedure<Annotation> {
	private InvocationFormatter formatter;
	private Method method;
	private Invocation invocation;

	public InvocationFormatProcedure(InvocationFormatter formatter, Invocation invocation) {
		super();
		this.formatter = formatter;
		this.invocation = invocation;
	}

	public InvocationFormatProcedure(InvocationFormatter formatter, Method method) {
		super();
		this.formatter = formatter;
		this.method = method;
	}

	public void initialize(int side, Process process, Annotation annotation, BeanFactory beanFactory, StreamFactory streamFactory, Header header, Channel channel) throws Exception {

	}

	public FormatInvocationOutputStream wrap(int side, Process process, Annotation annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, OutputStream outputStream) throws Exception {
		return new FormatInvocationOutputStream(formatter, header, outputStream, header.getCharset());
	}

	public FormatInvocationInputStream wrap(int side, Process process, Annotation annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, InputStream inputStream) throws Exception {
		return new FormatInvocationInputStream(formatter, header, inputStream, header.getCharset(), method);
	}

	public InvocationFormatter getFormatter() {
		return formatter;
	}

	public void setFormatter(InvocationFormatter formatter) {
		this.formatter = formatter;
	}

	public Invocation getInvocation() {
		return invocation;
	}

	public void setInvocation(Invocation invocation) {
		this.invocation = invocation;
	}

}
