package payne.framework.pigeon.generation;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.exception.BeanInitializeException;
import payne.framework.pigeon.generation.annotation.Name;

public class Interface extends Annotated implements Generable {
	private final String implementation;
	private final Class<?> type;
	private final Set<Function> functions;

	public Interface(String implementation, Class<?> interfase) throws BeanInitializeException {
		super(interfase.isAnnotationPresent(Name.class) ? interfase.getAnnotation(Name.class).value().trim() : interfase.getSimpleName(), interfase.getAnnotations());
		if (!interfase.isInterface()) {
			throw new IllegalArgumentException(interfase + " is not an interfase");
		}
		this.implementation = implementation;
		this.type = interfase;
		Set<Method> methods = Pigeons.getInterfaceDeclaredOpenableMethods(interfase);
		this.functions = new LinkedHashSet<Function>();
		for (Method method : methods) {
			this.functions.add(new Function(implementation, interfase, method));
		}
	}

	public String getImplementation() {
		return implementation;
	}

	public Class<?> getType() {
		return type;
	}

	public Set<Function> getFunctions() {
		return functions;
	}

}
