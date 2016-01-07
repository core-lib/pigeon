package payne.framework.pigeon.integration.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import payne.framework.pigeon.core.toolkit.IOToolkit;

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
 * @date 2015年11月8日 下午9:20:07
 *
 * @version 1.0.0
 */
public class HttpServletResponseOutputStream extends OutputStream {
	private final HttpServletResponse response;
	private final OutputStream outputStream;
	private final ByteArrayOutputStream header;
	private int count;

	public HttpServletResponseOutputStream(HttpServletResponse response) throws IOException {
		super();
		this.response = response;
		this.outputStream = response.getOutputStream();
		this.header = new ByteArrayOutputStream();
	}

	@Override
	public void write(int b) throws IOException {
		if (count >= 0) {
			header.write(b);
			if (b == '\r') {
				return;
			} else if (b == '\n') {
				count++;
				if (count == 2) {
					ByteArrayInputStream inputStream = new ByteArrayInputStream(header.toByteArray());
					String line = IOToolkit.readLine(inputStream);
					int code = Integer.valueOf(line.split("\\s+")[1]);
					response.setStatus(code);
					while ((line = IOToolkit.readLine(inputStream)) != null && !line.equals("")) {
						int index = line.indexOf(":");
						if (index <= 0) {
							continue;
						}
						String name = line.substring(0, index).trim();
						String value = line.substring(index + 1).trim();
						response.addHeader(name, value);
					}
					count = -1;
				}
			} else {
				count = 0;
			}
		} else {
			outputStream.write(b);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		int i = 0;
		for (; count >= 0 && i < b.length; i++) {
			write(b[i]);
		}
		if (i == 0) {
			outputStream.write(b);
		} else if (i < b.length) {
			outputStream.write(Arrays.copyOfRange(b, i, b.length));
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		int i = 0;
		for (; count >= 0 && i < len; i++) {
			write(b[off + i]);
		}
		if (i == 0) {
			outputStream.write(b, off, len);
		} else if (i < len) {
			outputStream.write(Arrays.copyOfRange(b, i, off + len));
		}
	}

	@Override
	public void flush() throws IOException {
		super.flush();
		outputStream.flush();
	}

}
