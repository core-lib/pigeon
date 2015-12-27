package payne.framework.pigeon.core.toolkit;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOToolkit {
	private static final Logger LOGGER = LoggerFactory.getLogger(IOToolkit.class);

	public static void close(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (Throwable e) {
			LOGGER.error("closing {} failed", closeable, e);
		}
	}

	public static void close(HttpURLConnection connection) {
		try {
			if (connection != null) {
				connection.disconnect();
			}
		} catch (Throwable e) {
			LOGGER.error("closing {} failed", connection, e);
		}
	}

	public static void close(Selector selector) {
		try {
			if (selector != null && selector.isOpen()) {
				selector.close();
			}
		} catch (Throwable e) {
			LOGGER.error("closing {} failed", selector, e);
		}
	}

	public static void close(Socket socket) {
		try {
			if (socket != null && !socket.isClosed() && socket.isConnected() && socket.isBound()) {
				socket.close();
			}
		} catch (Throwable e) {
			LOGGER.error("closing {} failed", socket, e);
		}
	}

	public static void close(DatagramSocket socket) {
		try {
			if (socket != null && !socket.isClosed() && socket.isConnected() && socket.isBound()) {
				socket.close();
			}
		} catch (Throwable e) {
			LOGGER.error("closing {} failed", socket, e);
		}
	}

	public static void close(ServerSocket server) {
		try {
			if (server != null && !server.isClosed()) {
				server.close();
			}
		} catch (Throwable e) {
			LOGGER.error("closing {} failed", server, e);
		}
	}

	public static void close(SelectionKey key) {
		try {
			if (key != null) {
				key.cancel();
				SelectableChannel channel = key.channel();
				if (channel != null) {
					channel.close();
				}
			}
		} catch (Throwable e) {
			LOGGER.error("canceling {} or closing {} failed", key, key.channel(), e);
		}
	}

	public static int transmit(InputStream readable, OutputStream writable) throws IOException {
		return transmit(readable, new byte[1024 * 8], writable);
	}

	public static int transmit(InputStream readable, int length, OutputStream writable) throws IOException {
		return transmit(readable, length, new byte[1024 * 8], writable);
	}

	public static int transmit(InputStream readable, byte[] buffer, OutputStream writable) throws IOException {
		int total = 0;
		int length = 0;
		while ((length = readable.read(buffer)) != -1) {
			writable.write(buffer, 0, length);
			total += length;
		}
		return total;
	}

	public static int transmit(InputStream readable, int length, byte[] buffer, OutputStream writable) throws IOException {
		int total = 0;
		int len = 0;
		while ((len = readable.read(buffer, 0, Math.min(length - total, buffer.length))) != -1) {
			writable.write(buffer, 0, len);
			total += len;
			if (total == length) {
				break;
			}
		}
		return total;
	}

	public static int transmit(InputStreamReader readable, OutputStreamWriter writable) throws IOException {
		return transmit(readable, new char[1024 * 8], writable);
	}

	public static int transmit(InputStreamReader readable, int length, OutputStreamWriter writable) throws IOException {
		return transmit(readable, length, new char[1024 * 8], writable);
	}

	public static int transmit(InputStreamReader readable, char[] buffer, OutputStreamWriter writable) throws IOException {
		int total = 0;
		int length = 0;
		while ((length = readable.read(buffer)) != -1) {
			writable.write(buffer, 0, length);
			total += length;
		}
		return total;
	}

	public static int transmit(InputStreamReader readable, int length, char[] buffer, OutputStreamWriter writable) throws IOException {
		int total = 0;
		int len = 0;
		while ((len = readable.read(buffer, 0, Math.min(length - total, buffer.length))) != -1) {
			writable.write(buffer, 0, len);
			total += len;
			if (total == length) {
				break;
			}
		}
		return total;
	}

	public static int transmit(Readable readable, Writable writable) throws IOException {
		return transmit(readable, new byte[1024 * 8], writable);
	}

	public static int transmit(Readable readable, int length, Writable writable) throws IOException {
		return transmit(readable, length, new byte[1024 * 8], writable);
	}

	public static int transmit(Readable readable, byte[] buffer, Writable writable) throws IOException {
		int total = 0;
		int length = 0;
		while ((length = readable.read(buffer)) != -1) {
			writable.write(buffer, 0, length);
			total += length;
		}
		return total;
	}

	public static int transmit(Readable readable, int length, byte[] buffer, Writable writable) throws IOException {
		int total = 0;
		int len = 0;
		while ((len = readable.read(buffer, 0, Math.min(length - total, buffer.length))) != -1) {
			writable.write(buffer, 0, len);
			total += len;
			if (total == length) {
				break;
			}
		}
		return total;
	}

	public static int append(OutputStream outputStream, String content) throws IOException {
		return append(outputStream, content, Charset.defaultCharset());
	}

	public static int append(OutputStream outputStream, String content, String charset) throws IOException {
		return append(outputStream, content, Charset.forName(charset));
	}

	public static int append(OutputStream outputStream, String content, Charset charset) throws IOException {
		return append(new OutputStreamWritable(outputStream), content, charset);
	}

	public static int append(Writable writable, String content) throws IOException {
		return append(writable, content, Charset.defaultCharset());
	}

	public static int append(Writable writable, String content, String charset) throws IOException {
		return append(writable, content, Charset.forName(charset));
	}

	public static int append(Writable writable, String content, Charset charset) throws IOException {
		byte[] bs = content.getBytes(charset);
		return writable.write(bs);
	}

	public static String readLine(InputStream inputStream) throws IOException {
		return readLine(inputStream, Charset.defaultCharset());
	}

	public static String readLine(InputStream inputStream, String charset) throws IOException {
		return readLine(inputStream, Charset.forName(charset));
	}

	public static String readLine(InputStream inputStream, Charset charset) throws IOException {
		return readLine(new InputStreamReadable(inputStream), charset);
	}

	public static String readLine(Readable readable) throws IOException {
		return readLine(readable, Charset.defaultCharset());
	}

	public static String readLine(Readable readable, String charset) throws IOException {
		return readLine(readable, Charset.forName(charset));
	}

	public static String readLine(Readable readable, Charset charset) throws IOException {
		ByteArrayOutputStream line = new ByteArrayOutputStream();
		int count = 0;
		int b = 0;
		position: while ((b = readable.read()) != -1) {
			count++;
			switch (b) {
			case '\r':
				continue;
			case '\n':
				break position;
			default:
				line.write(b);
				break;
			}
		}
		return count == 0 ? null : line.toString(charset.name());
	}

	public static void writeLine(String content, OutputStream outputStream) throws IOException {
		writeLine(content, outputStream, Charset.defaultCharset());
	}

	public static void writeLine(String content, OutputStream outputStream, String charset) throws IOException {
		writeLine(content, outputStream, Charset.forName(charset));
	}

	public static void writeLine(String content, OutputStream outputStream, Charset charset) throws IOException {
		writeLine(content, new OutputStreamWritable(outputStream), charset);
	}

	public static void writeLine(String content, Writable writable) throws IOException {
		writeLine(content, writable, Charset.defaultCharset());
	}

	public static void writeLine(String content, Writable writable, String charset) throws IOException {
		writeLine(content, writable, Charset.forName(charset));
	}

	public static void writeLine(String content, Writable writable, Charset charset) throws IOException {
		writable.write(content.getBytes(charset));
		writable.write(new byte[] { '\r', '\n' });
	}

	public static String toString(Reader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		char[] buffer = new char[1024 * 4];
		int length = 0;
		while ((length = reader.read(buffer)) != -1) {
			sb.append(buffer, 0, length);
		}
		return sb.toString();
	}

	public static String toString(InputStream inputStream) throws IOException {
		return toString(inputStream, Charset.defaultCharset());
	}

	public static String toString(InputStream inputStream, String charset) throws IOException {
		return toString(inputStream, Charset.forName(charset));
	}

	public static String toString(InputStream inputStream, Charset charset) throws IOException {
		return toString(new InputStreamReadable(inputStream), charset);
	}

	public static String toString(Readable readable) throws IOException {
		return toString(readable, Charset.defaultCharset());
	}

	public static String toString(Readable readable, String charset) throws IOException {
		return toString(readable, Charset.forName(charset));
	}

	public static String toString(Readable readable, Charset charset) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		transmit(readable, new OutputStreamWritable(out));
		return out.toString(charset.name());
	}

	public static String toHexString(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String temp = Integer.toHexString(bytes[i] & 0xFF);
			sb.append((temp.length() == 1) ? "0" + temp : temp);
		}
		return sb.toString().toUpperCase();
	}

	public static byte[] fromHexString(String hex) {
		int m = 0, n = 0;
		int length = hex.length() / 2;
		byte[] result = new byte[length];

		for (int i = 0; i < length; i++) {
			m = i * 2 + 1;
			n = m + 1;
			result[i] = (byte) (Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n)) & 0xFF);
		}

		return result;
	}

	public static void toHexString(InputStream in, OutputStream out) throws IOException {
		int b = 0;
		while ((b = in.read()) != -1) {
			String temp = Integer.toHexString(b & 0xFF);
			out.write(((temp.length() == 1) ? "0" + temp : temp).getBytes());
		}
	}

	public static void fromHexString(InputStream in, OutputStream out) throws IOException {
		while (true) {
			int m = in.read();
			if (m == -1) {
				break;
			}
			int n = in.read();
			if (n == -1) {
				throw new IOException("unexpected end of stream");
			}
			out.write(Integer.decode("0x" + Character.valueOf((char) m) + Character.valueOf((char) n)) & 0xFF);
		}
	}

	public static void delete(File file) throws IOException {
		if (file.isDirectory()) {
			for (File subfile : file.listFiles()) {
				delete(subfile);
			}
		}
		file.delete();
	}

}
