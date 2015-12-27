package payne.framework.pigeon.core.exception;


public class UnsupportedFormatException extends CodedException {
	private static final long serialVersionUID = -8387514249371652985L;

	private final String format;

	public UnsupportedFormatException(String format) {
		super("context unsupport data format " + format);
		this.format = format;
	}

	@Override
	public int getCode() {
		return 415;
	}

	@Override
	public String getReason() {
		return "Unsupported Media Type";
	}

	public String getFormat() {
		return format;
	}

}
