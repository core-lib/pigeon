package payne.framework.pigeon.server.exception;

import java.lang.reflect.Method;

import payne.framework.pigeon.core.annotation.Accept.Mode;
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
 * @date 2016年1月5日 下午12:18:09
 *
 * @version 1.0.0
 */
public class UnacceptableModeException extends CodedException {
	private static final long serialVersionUID = -8193340490630294413L;

	private final Method method;
	private final Mode mode;

	public UnacceptableModeException(Method method, Mode mode) {
		super("method " + method + " is not allow requested by using mode " + mode);
		this.method = method;
		this.mode = mode;
	}

	@Override
	public int getCode() {
		return 405;
	}

	@Override
	public String getReason() {
		return "Method Not Allowed";
	}

	public Method getMethod() {
		return method;
	}

	public Mode getMode() {
		return mode;
	}

}
