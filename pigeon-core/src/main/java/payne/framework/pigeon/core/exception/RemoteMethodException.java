package payne.framework.pigeon.core.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public class RemoteMethodException extends CodedException {
	private static final long serialVersionUID = -1773776072838408152L;

	private final int code;
	private final String reason;
	private final String traces;

	public RemoteMethodException(int code, String reason, String traces) {
		super("server response status code : " + code + " and message : " + reason);
		this.code = code;
		this.reason = reason;
		this.traces = traces;
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getReason() {
		return reason;
	}

	@Override
	public void printStackTrace() {
		super.printStackTrace();
	}

	@Override
	public void printStackTrace(PrintStream s) {
		super.printStackTrace(s);
		s.println(traces == null ? "" : "Caused by: " + traces);
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		super.printStackTrace(s);
		s.println(traces == null ? "" : "Caused by: " + traces);
	}

}
