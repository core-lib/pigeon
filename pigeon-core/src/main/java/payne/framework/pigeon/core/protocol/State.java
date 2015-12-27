package payne.framework.pigeon.core.protocol;

public class State {
	private int code = 200;
	private String message = "OK";

	public State() {
		super();
	}

	public State(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (code != other.code)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return code + (message == null || message.trim().equals("") ? "" : " " + message);
	}

}
