package payne.framework.pigeon.core.formatting;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "arguments")
public class Argument {
	private Object[] arguments;

	public Argument() {
		super();
	}

	public Argument(Object[] arguments) {
		super();
		this.arguments = arguments;
	}

	@XmlElement(name = "argument")
	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

}