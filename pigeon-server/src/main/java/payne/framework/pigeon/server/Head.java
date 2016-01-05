package payne.framework.pigeon.server;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import payne.framework.pigeon.server.exception.UnrecognizedHeadException;

public class Head {
	private final String method;
	private final String path;
	private final String parameter;
	private final String protocol;

	public Head(String head) throws IOException {
		head = URLDecoder.decode(head, Charset.defaultCharset().name());
		String[] segments = head.split("\\s+");
		if (segments.length != 3) {
			throw new UnrecognizedHeadException(head);
		}
		this.method = segments[0].trim().toUpperCase();

		int index = segments[1].indexOf('?');
		if (index == -1) {
			this.path = segments[1].trim();
			this.parameter = "";
		} else {
			this.path = segments[1].substring(0, index).trim();
			this.parameter = segments[1].substring(index + 1).trim();
		}
		this.protocol = segments[2].trim().toUpperCase();
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getParameter() {
		return parameter;
	}

	public String getProtocol() {
		return protocol;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Head other = (Head) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (parameter == null) {
			if (other.parameter != null)
				return false;
		} else if (!parameter.equals(other.parameter))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return method.toUpperCase() + " " + path + (parameter == null || parameter.trim().equals("") ? "" : "?" + parameter) + " " + protocol;
	}

}
