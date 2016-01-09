package payne.framework.pigeon.core.exception;

import payne.framework.pigeon.core.checking.Checker;

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
 * @date 2016年1月9日 下午6:45:50
 *
 * @version 1.0.0
 */
public class IllegalConfigException extends Exception {
	private static final long serialVersionUID = -4902591620585552415L;

	private final int code;
	private final Checker checker;

	public IllegalConfigException(String message, int code, Checker checker) {
		super(message);
		this.code = code;
		this.checker = checker;
	}

	public int getCode() {
		return code;
	}

	public Checker getChecker() {
		return checker;
	}

}
