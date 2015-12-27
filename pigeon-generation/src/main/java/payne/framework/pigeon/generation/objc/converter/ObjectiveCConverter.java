package payne.framework.pigeon.generation.objc.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import payne.framework.pigeon.generation.Converter;
import payne.framework.pigeon.generation.Generator;
import payne.framework.pigeon.generation.objc.ObjectiveCGenerator;

public abstract class ObjectiveCConverter implements Converter {
	protected ObjectiveCGenerator generator;
	protected Set<String> attributes;

	public boolean supports(Type type) {
		if (type == null) {
			return false;
		}
		if (type instanceof Class<?>) {
			return supports((Class<?>) type);
		} else if (type instanceof ParameterizedType) {
			return supports((ParameterizedType) type);
		} else {
			return false;
		}
	}

	protected boolean supports(Class<?> clazz) {
		return false;
	}

	protected boolean supports(ParameterizedType type) {
		return false;
	}

	public String convert(Type type) {
		if (type instanceof Class<?>) {
			return convert((Class<?>) type);
		} else if (type instanceof ParameterizedType) {
			return convert((ParameterizedType) type);
		} else {
			return null;
		}
	}

	protected String convert(Class<?> clazz) {
		throw new IllegalArgumentException("can not handle class " + clazz + " by using converter " + this);
	}

	protected String convert(ParameterizedType type) {
		throw new IllegalArgumentException("can not handle type " + type + " by using converter " + this);
	}

	public String convert(Type type, String name) {
		if (type instanceof Class<?>) {
			return convert((Class<?>) type, name);
		} else if (type instanceof ParameterizedType) {
			return convert((ParameterizedType) type, name);
		} else {
			return null;
		}
	}

	protected String convert(Class<?> clazz, String name) {
		return name + " ? " + name + " : " + "[NSNull null]";
	}

	protected String convert(ParameterizedType type, String name) {
		return name + " ? " + name + " : " + "[NSNull null]";
	}

	public boolean generable(Type type) {
		if (type instanceof Class<?>) {
			return generable((Class<?>) type);
		} else if (type instanceof ParameterizedType) {
			return generable((ParameterizedType) type);
		} else {
			throw new IllegalArgumentException("can not handle type " + type + " by using converter " + this);
		}
	}

	protected boolean generable(Class<?> clazz) {
		return false;
	}

	protected boolean generable(ParameterizedType type) {
		return false;
	}

	public Set<Class<?>> imports(Generator fg, Type type) {
		if (type instanceof Class<?>) {
			return imports(fg, (Class<?>) type);
		} else if (type instanceof ParameterizedType) {
			return imports(fg, (ParameterizedType) type);
		} else {
			throw new IllegalArgumentException("can not handle type " + type + " by using converter " + this);
		}
	}

	protected Set<Class<?>> imports(Generator fg, Class<?> clazz) {
		return new HashSet<Class<?>>();
	}

	protected Set<Class<?>> imports(Generator fg, ParameterizedType type) {
		return new HashSet<Class<?>>();
	}

	public Set<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<String> attributes) {
		this.attributes = attributes;
	}

	public ObjectiveCGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(ObjectiveCGenerator generator) {
		this.generator = generator;
	}

}
