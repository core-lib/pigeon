package payne.framework.pigeon.core.dynamic;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.exception.DynamicInvocationBuildException;

public class NormalDynamicInvocationBuilder implements DynamicInvocationBuilder {
	private DynamicInvocationClassGenerator dynamicInvocationClassGenerator;
	private DynamicInvocationArchiver dynamicInvocationArchiver;

	public NormalDynamicInvocationBuilder() {
		super();
		this.dynamicInvocationClassGenerator = new ASMDynamicInvocationClassGenerator();
		this.dynamicInvocationArchiver = new JARDynamicInvocationArchiver();
	}

	public NormalDynamicInvocationBuilder(DynamicInvocationClassGenerator dynamicInvocationClassGenerator, DynamicInvocationArchiver dynamicInvocationArchiver) {
		super();
		this.dynamicInvocationClassGenerator = dynamicInvocationClassGenerator;
		this.dynamicInvocationArchiver = dynamicInvocationArchiver;
	}

	public void build(String implementation, Class<?> interfase) throws DynamicInvocationBuildException {
		try {
			Set<Method> methods = Pigeons.getClassAllOpenableMethods(interfase);
			Set<Generation> generations = new HashSet<Generation>();
			for (Method method : methods) {
				Generation generation = dynamicInvocationClassGenerator.generate(implementation, interfase, method);
				generations.add(generation);
			}
			dynamicInvocationArchiver.archive(interfase.getSimpleName(), generations);
		} catch (Exception e) {
			throw new DynamicInvocationBuildException(e, interfase);
		}
	}

	public DynamicInvocationClassGenerator getDynamicInvocationClassGenerator() {
		return dynamicInvocationClassGenerator;
	}

	public void setDynamicInvocationClassGenerator(DynamicInvocationClassGenerator dynamicInvocationClassGenerator) {
		this.dynamicInvocationClassGenerator = dynamicInvocationClassGenerator;
	}

	public DynamicInvocationArchiver getDynamicInvocationArchiver() {
		return dynamicInvocationArchiver;
	}

	public void setDynamicInvocationArchiver(DynamicInvocationArchiver dynamicInvocationArchiver) {
		this.dynamicInvocationArchiver = dynamicInvocationArchiver;
	}

}
