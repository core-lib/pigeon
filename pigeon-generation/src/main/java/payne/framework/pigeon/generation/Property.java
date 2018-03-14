package payne.framework.pigeon.generation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class Property extends Annotated {
    private final Type type;
    private final String comment;

    public Property(Type type, String comment, String name, Annotation[] annotations) {
        super(name, annotations);
        this.type = type;
        this.comment = comment;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String getComment() {
        return comment;
    }
}
