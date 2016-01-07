package payne.framework.pigeon.integration.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author yangchangpei 646742615@qq.com
 *
 * @date 2015年11月9日 上午9:59:26
 *
 * @version 1.0.0
 */
public class HttpServletRequestInputStream extends InputStream {
	private final HttpServletRequest request;
	private final InputStream inputStream;
	private final byte[] header;
	private int position;

	public HttpServletRequestInputStream(HttpServletRequest request) throws IOException {
		super();
		this.request = request;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		Enumeration<?> enumeration = this.request.getHeaderNames();
		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement().toString();
			String value = this.request.getHeader(name);
			if ("Transfer-Encoding".equalsIgnoreCase(name) && "chunked".equalsIgnoreCase(value)) {
				ps.append("Content-Length").append(": ").append(String.valueOf(Integer.MAX_VALUE)).println();
			} else {
				ps.append(name).append(": ").append(value).println();
			}
		}

		InputStream in = request.getInputStream();
		if (in == null) {
			StringBuilder builder = new StringBuilder();
			for (Entry<String, String[]> entry : this.request.getParameterMap().entrySet()) {
				for (int i = 0; entry.getKey() != null && entry.getValue() != null && i < entry.getValue().length; i++) {
					if (builder.length() > 0) {
						builder.append("&");
					}
					builder.append(entry.getKey()).append("=").append(entry.getValue()[i]);
				}
			}
			byte[] body = builder.toString().getBytes();
			this.inputStream = new ByteArrayInputStream(body);
			ps.append("Content-Length").append(": ").append(String.valueOf(body.length)).println();
		} else {
			this.inputStream = in;
		}

		ps.println();
		ps.flush();
		this.header = baos.toByteArray();
	}

	@Override
	public int read() throws IOException {
		if (position < header.length) {
			return header[position++] & 0xff;
		}
		return inputStream.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		int i = 0;
		for (; position < header.length && i < b.length; i++) {
			b[i] = (byte) read();
		}
		if (i < b.length) {
			return i + inputStream.read(b, i, b.length - i);
		} else {
			return i;
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int i = 0;
		for (; position < header.length && i < len; i++) {
			b[off + i] = (byte) read();
		}
		if (i < len) {
			return i + inputStream.read(b, off + i, len - i);
		} else {
			return i;
		}
	}

}
