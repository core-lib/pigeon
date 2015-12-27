package payne.framework.pigeon.core.formatting;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;

import payne.framework.pigeon.core.Conversion;
import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.exception.FormatterException;

/**
 * 泛型数据格式化器,实现该接口的实现类提供指定泛型的序列化和反序列化
 * 
 * @author yangchangpei
 * 
 * @param <T>
 *            数据类型
 */
public interface DataFormatter<T extends Serializable> extends Conversion {

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
	 * @param out
	 *            输出流
	 * @throws FormatterException
	 *             数据格式化异常
	 */
	void serialize(Header header, T data, OutputStream out, String charset) throws FormatterException;

	/**
	 * 反序列化,实现该方法不应该关闭输入流参数,让框架自行关闭
	 * 
	 * @param in
	 *            输入流
	 * @param method
	 *            对应的方法
	 * @return 将数据反序列化成T类型的对象
	 * @throws FormatterException
	 *             数据格式化异常
	 */
	T deserialize(Header header, InputStream in, String charset, Method method) throws FormatterException;

}
