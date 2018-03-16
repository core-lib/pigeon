package payne.framework.pigeon.core;

import payne.framework.pigeon.core.toolkit.CaseIgnoredMap;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 请求/回应头
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-03-16 11:10
 **/
public class Header extends CaseIgnoredMap<List<String>> {
    private final DateFormat dateFormat;

    public Header() {
        this.dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public Header(Map<String, List<String>> map) {
        this();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            for (String value : entry.getValue()) {
                add(entry.getKey(), value);
            }
        }
    }

    public boolean containsKeyIgnoreCase(String key) {
        if (key == null) {
            return containsKey(key);
        }
        for (String name : keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public String getIgnoreCase(String key) {
        if (key == null) return null;
        for (String name : keySet()) {
            if (key.equalsIgnoreCase(name)) {
                List<String> values = get(name);
                return values != null && values.size() > 0 ? values.get(0) : null;
            }
        }
        return null;
    }

    public void put(String key, String value) {
        put(key, new ArrayList<String>(Collections.singleton(value)));
    }

    public void add(String key, String value) {
        List<String> values = get(key);
        if (values == null) put(key, values = new ArrayList<String>());
        values.add(value);
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
        return contentType != null ? contentType.split(";")[0] : null;
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
        return contentType != null && contentType.contains("=") ? contentType.split("=")[1] : Charset.defaultCharset().name();
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

}
