package payne.framework.pigeon.core.formatting;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.exception.FormatterException;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

public class Hessian2InvocationFormatter implements InvocationFormatter {

	public String algorithm() {
		return "application/hessian2";
	}

	public void serialize(Header header, Invocation data, OutputStream out, String charset) throws FormatterException {
		Hessian2Output oos = null;
		try {
			oos = new Hessian2Output(out);
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

	public Invocation deserialize(Header header, InputStream in, String charset, Method method) throws FormatterException {
		Hessian2Input ois = null;
		try {
			ois = new Hessian2Input(in);
			return (Invocation) ois.readObject();
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
