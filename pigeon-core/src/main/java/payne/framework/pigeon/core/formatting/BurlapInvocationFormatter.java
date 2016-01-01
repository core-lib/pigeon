package payne.framework.pigeon.core.formatting;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.exception.FormatterException;

import com.caucho.burlap.io.BurlapInput;
import com.caucho.burlap.io.BurlapOutput;

public class BurlapInvocationFormatter implements InvocationFormatter {

	public String algorithm() {
		return "application/burlap";
	}

	public void serialize(Object data, OutputStream out, String charset) throws FormatterException {
		BurlapOutput oos = null;
		try {
			oos = new BurlapOutput(out);
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
		BurlapInput ois = null;
		try {
			ois = new BurlapInput(in);
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
