package payne.framework.pigeon.core.exception;

import payne.framework.pigeon.core.Task;

public class TaskRevokeException extends Exception {
	private static final long serialVersionUID = 7917685518229818202L;

	private final Task task;

	public TaskRevokeException(Task task) {
		super();
		this.task = task;
	}

	public TaskRevokeException(String message, Task task) {
		super(message);
		this.task = task;
	}

	public TaskRevokeException(Throwable cause, Task task) {
		super(cause);
		this.task = task;
	}

	public TaskRevokeException(String message, Throwable cause, Task task) {
		super(message, cause);
		this.task = task;
	}

	public Task getTask() {
		return task;
	}

}
