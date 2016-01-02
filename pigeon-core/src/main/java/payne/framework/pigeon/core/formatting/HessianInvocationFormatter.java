package payne.framework.pigeon.core.formatting;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.exception.FormatterException;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

public class HessianInvocationFormatter implements InvocationFormatter {

	public String algorithm() {
		return "application/hessian";
	}

	public void serialize(Object data, Structure structure, OutputStream out, String charset) throws FormatterException {
		HessianOutput oos = null;
		try {
			oos = new HessianOutput(out);
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
		HessianInput ois = null;
		try {
			ois = new HessianInput(in);
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
