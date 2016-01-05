package payne.framework.pigeon.server.exception;

import java.lang.reflect.Method;

import payne.framework.pigeon.core.exception.CodedException;

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
 * @date 2016年1月5日 下午2:38:54
 *
 * @version 1.0.0
 */
public class UnacceptableMediumException extends CodedException {
	private static final long serialVersionUID = 2903200601600560585L;

	private final Method method;
	private final String medium;

	public UnacceptableMediumException(Method method, String medium) {
		super("method " + method + " unsupported media type " + medium);
		this.method = method;
		this.medium = medium;
	}

	@Override
	public int getCode() {
		return 415;
	}

	@Override
	public String getReason() {
		return "Unsupported Media Type";
	}

	public Method getMethod() {
		return method;
	}

	public String getMedium() {
		return medium;
	}

}
