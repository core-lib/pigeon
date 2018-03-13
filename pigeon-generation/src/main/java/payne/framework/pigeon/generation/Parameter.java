package payne.framework.pigeon.generation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class Parameter extends Annotated {
	private final Type type;

	public Parameter(String name, Annotation[] annotations, Type type) {
		super(name, annotations);
		this.type = type;
	}

	public Type getType() {
		return type;
	}

    @Override
    public String getComment() {
        return null;
    }
}
