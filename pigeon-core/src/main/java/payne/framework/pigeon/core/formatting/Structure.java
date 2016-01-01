package payne.framework.pigeon.core.formatting;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public final class Structure {
	public final Form form;
	public final Type[] types;
	public final Annotation[][] annotations;

	private Structure(Form form, Type[] types, Annotation[][] annotations) {
		this.form = form;
		this.types = types;
		this.annotations = annotations;
	}

	public static Structure forValue(Type type, Annotation[] annotations) {
		return new Structure(Form.VALUE, new Type[] { type }, new Annotation[][] { annotations });
	}

	public static Structure forArray(Type[] types, Annotation[][] annotations) {
		return new Structure(Form.ARRAY, types, annotations);
	}

	public static enum Form {
		VALUE, ARRAY;
	}

}
