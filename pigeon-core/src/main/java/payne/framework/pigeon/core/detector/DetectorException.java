package payne.framework.pigeon.core.detector;

import java.io.IOException;

/**
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author yangchangpei 646742615@qq.com
 *
 * @date 2015年11月8日 下午6:31:05
 *
 * @version 1.0.0
 */
public class DetectorException extends IOException {
	private static final long serialVersionUID = -3085416459682626800L;

	public DetectorException() {
		super();
	}

	public DetectorException(String message, Throwable cause) {
		super(message, cause);
	}

	public DetectorException(String message) {
		super(message);
	}

	public DetectorException(Throwable cause) {
		super(cause);
	}

}
