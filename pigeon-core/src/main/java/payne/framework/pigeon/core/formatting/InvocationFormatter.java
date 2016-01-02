package payne.framework.pigeon.core.formatting;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Conversion;
import payne.framework.pigeon.core.exception.FormatterException;

/**
 * 调用数据格式化器
 * 
 * @author yangchangpei
 *
 */
public interface InvocationFormatter extends Conversion {
	/**
	 * 数据格式,如json:application/json xml:application/xml
	 * 等,此值应该与其他的DataFormatter的不相同,也就是子类需要保证各自唯一
	 * 
	 * @return 数据格式
	 */
	String algorithm();

	/**
	 * 序列化,实现该方法不应该关闭输入流参数,让框架自行关闭
	 * 
	 * @param data
	 *            需要序列化到输出流里面去的java对象
	 * @param structure
	 *            数据结构
	 * @param out
	 *            输出流
	 * @param charset
	 *            字符集
	 * @throws FormatterException
	 *             数据格式化异常
	 */
	void serialize(Object data, Structure structure, OutputStream out, String charset) throws FormatterException;

	/**
	 * 反序列化,实现该方法不应该关闭输入流参数,让框架自行关闭
	 * 
	 * @param structure
	 *            数据结构
	 * @param in
	 *            输入流
	 * @param charset
	 *            字符集
	 * @return 将数据反序列化成T类型的对象
	 * @throws FormatterException
	 *             数据格式化异常
	 */
	Object deserialize(Structure structure, InputStream in, String charset) throws FormatterException;
}
