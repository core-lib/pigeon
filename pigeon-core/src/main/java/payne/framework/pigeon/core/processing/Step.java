package payne.framework.pigeon.core.processing;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.protocol.Channel;

public class Step implements Comparable<Step> {
	private final Process process;
	private final Annotation annotation;
	private final Procedure<Annotation> procedure;

	public Step(Process process, Annotation annotation, Procedure<Annotation> procedure) {
		super();
		this.process = process;
		this.annotation = annotation;
		this.procedure = procedure;
	}

	public void initialize(int side, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header) throws Exception {
		procedure.initialize(side, process, annotation, beanFactory, streamFactory, header, channel);
	}

	public OutputStream wrap(int side, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, OutputStream outputStream) throws Exception {
		return procedure.wrap(side, process, annotation, beanFactory, streamFactory, channel, header, outputStream);
	}

	public InputStream wrap(int side, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, InputStream inputStream) throws Exception {
		return procedure.wrap(side, process, annotation, beanFactory, streamFactory, channel, header, inputStream);
	}

	public Process getProcess() {
		return process;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public Procedure<Annotation> getProcedure() {
		return procedure;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((process == null) ? 0 : process.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Step other = (Step) obj;
		if (process == null) {
			if (other.process != null)
				return false;
		} else if (!process.equals(other.process))
			return false;
		return true;
	}

	public int compareTo(Step other) {
		return process.step() > other.process.step() ? 1 : process.step() < other.process.step() ? -1 : 0;
	}

}
