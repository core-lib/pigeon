package payne.framework.pigeon.server;

/**
 * 特性
 * 
 * @author yangchangpei
 *
 */
public enum Feature {

	/**
	 * 允许连接长连并且重复使用
	 */
	ALLOW_CONNECTION_KEEP_ALIVE(true);

	/**
	 * 默认模式
	 */
	public final boolean defaultMode;

	private Feature(boolean defaultMode) {
		this.defaultMode = defaultMode;
	}

}
