package payne.framework.pigeon.core.formatting.hessian;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.exception.FormatterException;
import payne.framework.pigeon.core.formatting.InvocationFormatter;
import payne.framework.pigeon.core.formatting.Structure;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

public class Hessian2InvocationFormatter implements InvocationFormatter {

	public String algorithm() {
		return "application/hessian2";
	}

	public void serialize(Object data, Structure structure, OutputStream out, String charset) throws FormatterException {
		Hessian2Output oos = null;
		try {
			oos = new Hessian2Output(out);
			oos.writeObject(data);
		} catch (Exception e) {
			throw new FormatterException(e, this, data);
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
				throw new FormatterException(e, this, data);
			}
		}
	}

	public Object deserialize(Structure structure, InputStream in, String charset) throws FormatterException {
		Hessian2Input ois = null;
		try {
			ois = new Hessian2Input(in);
			return ois.readObject();
		} catch (Exception e) {
			throw new FormatterException(e, this, in, structure);
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
				throw new FormatterException(e, this, in, structure);
			}
		}
	}

}
