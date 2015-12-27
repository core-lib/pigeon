package payne.framework.pigeon.core;

import payne.framework.pigeon.core.exception.TaskExecuteException;
import payne.framework.pigeon.core.exception.TaskRevokeException;

public interface Task {

	void execute() throws TaskExecuteException;

	void revoke() throws TaskRevokeException;

}
