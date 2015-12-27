package payne.framework.pigeon.core.exception;


/**
 * 没有映射的路径异常
 * 
 * @author ron
 * 
 */
public class UnmappedPathException extends CodedException {
	private static final long serialVersionUID = 8726675305413844932L;

	private final String path;

	public UnmappedPathException(String path) {
		super("path:" + path + " is an unmapped path");
		this.path = path;
	}

	@Override
	public int getCode() {
		return 404;
	}

	@Override
	public String getReason() {
		return "Not Found";
	}

	public String getPath() {
		return path;
	}

}
