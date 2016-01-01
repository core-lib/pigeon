package payne.framework.pigeon.core.formatting;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.exception.FormatterException;
import payne.framework.pigeon.core.toolkit.IOToolkit;

/**
 * 对象格式的调用对象格式化器,该格式化器只对有transient修饰符的字段起忽略作用
 * 
 * @author ron
 * 
 */
public class ObjectInvocationFormatter implements InvocationFormatter {

	public String algorithm() {
		return "application/x-java-serialized-object";
	}

	public void serialize(Header header, Object data, OutputStream out, String charset) throws FormatterException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(out);
			oos.writeObject(data);
		} catch (Exception e) {
			throw new FormatterException(e, this, data, null);
		} finally {
			IOToolkit.close(oos);
		}
	}

	public Object deserialize(Header header, InputStream in, String charset, Method method) throws FormatterException {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(in);
			return ois.readObject();
		} catch (Exception e) {
			throw new FormatterException(e, this, in, method);
		} finally {
			IOToolkit.close(ois);
		}
	}

}
