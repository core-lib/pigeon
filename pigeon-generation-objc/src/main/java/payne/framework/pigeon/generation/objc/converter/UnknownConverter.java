package payne.framework.pigeon.generation.objc.converter;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import payne.framework.pigeon.generation.Generator;

public class UnknownConverter extends ObjectiveCConverter {

	@Override
	public boolean supports(Type type) {
		return true;
	}

	@Override
	public String convert(Type type) {
		return "id";
	}

	@Override
	public String convert(Type type, String name) {
		return name;
	}

	@Override
	public boolean generable(Type type) {
		return false;
	}

	@Override
	public Set<Class<?>> imports(Generator fg, Type type) {
		return new HashSet<Class<?>>();
	}

}
