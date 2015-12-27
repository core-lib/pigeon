package payne.framework.pigeon.generation;

import java.lang.reflect.Type;

public interface Converter {

	boolean supports(Type type);

	String convert(Type type);

}
