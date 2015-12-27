package payne.framework.pigeon.core.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

import payne.framework.pigeon.core.toolkit.IOToolkit;

public abstract class TransferableChannel extends AttributedChannel {
	protected InputStream inputStream;
	protected OutputStream outputStream;

	protected int readed;
	protected int writed;
	protected State state;

	protected boolean timeouted = false;

	public int getReaded() {
		return readed;
	}

	public int getWrited() {
		return writed;
	}

	public int read() throws IOException {
		try {
			if (timeouted) {
				throw new SocketTimeoutException();
			}
			int c = inputStream.read();
			if (c != -1) {
				readed++;
			}
			return c;
		} catch (IOException e) {
			if (timeouted) {
				throw new SocketTimeoutException();
			}
			throw e;
		}
	}

	public int read(byte[] b) throws IOException {
		try {
			if (timeouted) {
				throw new SocketTimeoutException();
			}
			int c = inputStream.read(b);
			if (c != -1) {
				readed += c;
			}
			return c;
		} catch (IOException e) {
			if (timeouted) {
				throw new SocketTimeoutException();
			}
			throw e;
		}
	}

	public int read(byte[] b, int off, int len) throws IOException {
		try {
			if (timeouted) {
				throw new SocketTimeoutException();
			}
			len = Math.min(b.length - off, len);
			int c = inputStream.read(b, off, len);
			if (c != -1) {
				readed += c;
			}
			return c;
		} catch (IOException e) {
			if (timeouted) {
				throw new SocketTimeoutException();
			}
			throw e;
		}
	}

	public int write(int b) throws IOException {
		try {
			if (timeouted) {
				throw new SocketTimeoutException();
			}
			outputStream.write(b);
			writed++;
			return 1;
		} catch (IOException e) {
			if (timeouted) {
				throw new SocketTimeoutException();
			}
			throw e;
		}
	}

	public int write(byte[] b) throws IOException {
		try {
			if (timeouted) {
				throw new SocketTimeoutException();
			}
			outputStream.write(b);
			writed += b.length;
			return b.length;
		} catch (IOException e) {
			if (timeouted) {
				throw new SocketTimeoutException();
			}
			throw e;
		}
	}

	public int write(byte[] b, int off, int len) throws IOException {
		try {
			if (timeouted) {
				throw new SocketTimeoutException();
			}
			len = Math.min(b.length - off, len);
			outputStream.write(b, off, len);
			writed += len;
			return len;
		} catch (IOException e) {
			if (timeouted) {
				throw new SocketTimeoutException();
			}
			throw e;
		}
	}

	public void timeout() throws IOException {
		timeouted = true;
		close();
	}

	public void close() throws IOException {
		IOToolkit.close(inputStream);
		IOToolkit.close(outputStream);
	}

}
