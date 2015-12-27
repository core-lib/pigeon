package payne.framework.pigeon.core.exception;

import java.util.Set;

import payne.framework.pigeon.core.dynamic.Generation;

public class DynamicInvocationArchiveException extends Exception {
	private static final long serialVersionUID = -7204351524362507257L;

	private final Set<Generation> generations;

	public DynamicInvocationArchiveException(String message, Set<Generation> generations) {
		super(message);
		this.generations = generations;
	}

	public DynamicInvocationArchiveException(Throwable cause, Set<Generation> generations) {
		super(cause);
		this.generations = generations;
	}

	public DynamicInvocationArchiveException(String message, Throwable cause, Set<Generation> generations) {
		super(message, cause);
		this.generations = generations;
	}

	public Set<Generation> getGenerations() {
		return generations;
	}

}
