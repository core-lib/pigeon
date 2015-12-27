package payne.framework.pigeon.generation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class Property extends Annotated {
	private final Type type;

	public Property(Type type, String name, Annotation[] annotations) {
		super(name, annotations);
		this.type = type;
	}

	public Type getType() {
		return type;
	}

}
