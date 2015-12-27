package payne.framework.pigeon.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import payne.framework.pigeon.client.exception.UncompletedInvokeException;
import payne.framework.pigeon.client.exception.UnexecutedInvokeException;

public abstract class Invoker<V> implements Future<V>, Callable<V> {
	private static ExecutorService executor = Executors.newCachedThreadPool();

	private Future<V> future;

	/**
	 * can not cancel an invocation while it is running so if the invocation has
	 * been started you must wait for it complete by itself, but it can cancel
	 * an invocation which has not been started ,thus argument force will be
	 * ignored by just use as false
	 */
	public boolean cancel(boolean force) throws UnexecutedInvokeException {
		if (future == null) {
			throw new UnexecutedInvokeException(this);
		}
		return cancel(false);
	}

	public boolean isCancelled() throws UnexecutedInvokeException {
		if (future == null) {
			throw new UnexecutedInvokeException(this);
		}
		return future.isCancelled();
	}

	public boolean isDone() throws UnexecutedInvokeException {
		if (future == null) {
			throw new UnexecutedInvokeException(this);
		}
		return future.isDone();
	}

	public V get() throws InterruptedException, ExecutionException, UnexecutedInvokeException {
		if (future == null) {
			throw new UnexecutedInvokeException(this);
		}
		return future.get();
	}

	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException, UnexecutedInvokeException {
		if (future == null) {
			throw new UnexecutedInvokeException(this);
		}
		return future.get(timeout, unit);
	}

	/**
	 * invoke the server remote method and just simply return this
	 * 
	 * @return this
	 * @throws UncompletedInvokeException
	 *             if this invocation is running or undone
	 */
	public synchronized Invoker<V> invoke() throws UncompletedInvokeException {
		if (future != null && !future.isDone()) {
			throw new UncompletedInvokeException(this);
		}
		future = executor.submit(this);
		return this;
	}

	public static ExecutorService getExecutor() {
		return executor;
	}

	public static void setExecutor(ExecutorService executor) {
		Invoker.executor = executor;
	}

}
