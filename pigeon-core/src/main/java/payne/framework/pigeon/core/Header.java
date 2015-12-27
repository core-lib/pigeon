package payne.framework.pigeon.core;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public final class Header implements Map<String, String>, Constants {
	private final DateFormat dateFormat;

	private transient final Map<String, String> properties = new LinkedHashMap<String, String>();

	public Header() {
		this.dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public int size() {
		return properties.size();
	}

	public boolean isEmpty() {
		return properties.isEmpty();
	}

	public boolean containsKey(Object key) {
		return properties.containsKey(key);
	}

	public boolean containsKeyIgnoreCase(String key) {
		if (key == null) {
			return containsKey(key);
		}
		for (String name : properties.keySet()) {
			if (key.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsValue(Object value) {
		return properties.containsValue(value);
	}

	public String get(Object key) {
		return properties.get(key);
	}

	public String getIgnoreCase(String key) {
		if (key == null) {
			return get(key);
		}
		for (String name : properties.keySet()) {
			if (key.equalsIgnoreCase(name)) {
				return properties.get(name);
			}
		}
		return null;
	}

	public String put(String key, String value) {
		if (key == null || value == null) {
			throw new IllegalArgumentException("key or value must not be null");
		}
		return properties.put(key, value);
	}

	public String remove(Object key) {
		return properties.remove(key);
	}

	public void putAll(Map<? extends String, ? extends String> m) {
		properties.putAll(m);
	}

	public void clear() {
		properties.clear();
	}

	public Set<String> keySet() {
		return properties.keySet();
	}

	public Collection<String> values() {
		return properties.values();
	}

	public Set<Entry<String, String>> entrySet() {
		return properties.entrySet();
	}

	public String getHost() {
		return getIgnoreCase("Host");
	}

	public void setHost(String host) {
		put("Host", host);
	}

	public String getUserAgent() {
		return getIgnoreCase("User-Agent");
	}

	public void setUserAgent(String userAgent) {
		put("User-Agent", userAgent);
	}

	public String getServer() {
		return getIgnoreCase("Server");
	}

	public void setServer(String server) {
		put("Server", server);
	}

	public String getPragma() {
		return getIgnoreCase("Pragma");
	}

	public void setPragma(String pragma) {
		put("Pragma", pragma);
	}

	public String getCacheControl() {
		return getIgnoreCase("Cache-Control");
	}

	public void setCacheControl(String cacheControl) {
		put("Cache-Control", cacheControl);
	}

	public Date getDate() {
		try {
			String date = getIgnoreCase("Date");
			return date == null ? null : dateFormat.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setDate(Date date) {
		put("Date", dateFormat.format(date));
	}

	public String getContentType() {
		String contentType = getIgnoreCase("Content-Type");
		return contentType != null ? contentType.split(";")[0] : "application/url";
	}

	public void setContentType(String contentType) {
		if (contentType == null || contentType.matches(".*[;=].*")) {
			throw new IllegalArgumentException("illegal format " + contentType + " for it must not null or contains [ ; = ]");
		}
		String charset = getCharset();
		put("Content-Type", contentType + ";charset=" + charset);
	}

	public String getCharset() {
		String contentType = getIgnoreCase("Content-Type");
		String charset = contentType != null && contentType.contains("=") ? contentType.split("=")[1] : Charset.defaultCharset().name();
		return charset;
	}

	public void setCharset(String charset) {
		if (charset == null || charset.matches(".*[;=].*")) {
			throw new IllegalArgumentException("illegal charset " + charset + " for it must not null or contains [ ; = ]");
		}
		String format = getContentType();
		String contentType = format + ";charset=" + charset;
		put("Content-Type", contentType);
	}

	public String getTransferEncoding() {
		return getIgnoreCase("Transfer-Encoding");
	}

	public void setTransferEncoding(String transferEncoding) {
		put("Transfer-Encoding", transferEncoding);
	}

	public int getContentLength() {
		return containsKeyIgnoreCase("Content-Length") ? Integer.valueOf(getIgnoreCase("Content-Length")) : 0;
	}

	public void setContentLength(int contentLength) {
		put("Content-Length", String.valueOf(contentLength));
	}

	public String getConnection() {
		return containsKeyIgnoreCase("Connection") ? getIgnoreCase("Connection") : "closed";
	}

	public void setConnection(String connection) {
		put("Connection", connection);
	}

	@Override
	public String toString() {
		return properties.toString();
	}

}
