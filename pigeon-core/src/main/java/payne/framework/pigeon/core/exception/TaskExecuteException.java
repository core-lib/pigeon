package payne.framework.pigeon.core.exception;

import payne.framework.pigeon.core.Task;


public class TaskExecuteException extends Exception {
	private static final long serialVersionUID = 4462555811579128164L;

	private final Task task;

	public TaskExecuteException(Task task) {
		super();
		this.task = task;
	}

	public TaskExecuteException(String message, Task task) {
		super(message);
		this.task = task;
	}

	public TaskExecuteException(Throwable cause, Task task) {
		super(cause);
		this.task = task;
	}

	public TaskExecuteException(String message, Throwable cause, Task task) {
		super(message, cause);
		this.task = task;
	}

	public Task getTask() {
		return task;
	}

}
