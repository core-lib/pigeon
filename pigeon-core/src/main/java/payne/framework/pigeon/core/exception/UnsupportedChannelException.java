package payne.framework.pigeon.core.exception;


public class UnsupportedChannelException extends CodedException {
	private static final long serialVersionUID = -5461993817304650849L;

	private final String protocol;

	public UnsupportedChannelException(String protocol) {
		super("unsupport channel with protocol:" + protocol);
		this.protocol = protocol;
	}

	@Override
	public int getCode() {
		return 405;
	}

	@Override
	public String getReason() {
		return "Method Not Allowed";
	}

	public String getProtocol() {
		return protocol;
	}

}
