package payne.framework.pigeon.generation;

import payne.framework.pigeon.generation.annotation.Note;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Annotated implements Named, Noted, Commented {
    protected final String name;
    protected final String[] notes;
    protected final Set<Annotation> annotations;

	protected Annotated(String name, Annotation[] annotations) {
		this.name = name;
		this.annotations = annotations == null ? new LinkedHashSet<Annotation>() : new LinkedHashSet<Annotation>(Arrays.asList(annotations));
		for (Annotation annotation : this.annotations) {
			if (annotation instanceof Note) {
				this.notes = ((Note) annotation).value();
				return;
			}
		}
		this.notes = null;
	}

	public String getName() {
		return name;
	}

	public String[] getNotes() {
		return notes;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

}
