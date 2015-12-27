package payne.framework.pigeon.core;

public interface Transcoder {

	/**
	 * 获取传输数据的字符集
	 * 
	 * @return 传输数据的字符集
	 */
	String getCharset();

	/**
	 * 设置传输数据的字符集
	 * 
	 * @param charset
	 *            传输数据的字符集
	 */
	void setCharset(String charset);

}
