package payne.framework.pigeon.core.formatting;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.exception.FormatterException;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

public class HessianInvocationFormatter implements InvocationFormatter {

	public String algorithm() {
		return "application/hessian";
	}

	public void serialize(Header header, Object data, OutputStream out, String charset) throws FormatterException {
		HessianOutput oos = null;
		try {
			oos = new HessianOutput(out);
			oos.writeObject(data);
		} catch (Exception e) {
			throw new FormatterException(e, this, data, null);
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
				throw new FormatterException(e, this, data, null);
			}
		}
	}

	public Object deserialize(Header header, InputStream in, String charset, Method method) throws FormatterException {
		HessianInput ois = null;
		try {
			ois = new HessianInput(in);
			return ois.readObject();
		} catch (Exception e) {
			throw new FormatterException(e, this, in, method);
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
				throw new FormatterException(e, this, in, method);
			}
		}
	}

}
