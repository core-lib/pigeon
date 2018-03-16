package payne.framework.pigeon.core;

import java.util.ArrayList;
import java.util.List;

public class Cookie {
    private String name;
    private String value;
    private String comment;
    private String domain;
    private int maxage = -1;
    private String path;
    private boolean secure = false;
    private int version = 0;
    private long time = System.currentTimeMillis();

    public Cookie(String cookie) {
        String[] pairs = cookie.split("\\s*;\\s*");

        String[] nv = pairs[0].split("\\s*=\\s*");
        if (nv.length != 2) {
            return;
        }
        name = nv[0];
        value = nv[1];

        for (int i = 1; i < pairs.length; i++) {
            String pair = pairs[i];
            if (pair.trim().equals("")) {
                continue;
            }
            String[] splits = pair.split("=");
            if (splits[0].equalsIgnoreCase("Secure") && splits.length == 1) {
                secure = true;
                continue;
            }
            if (splits.length != 2) {
                continue;
            }
            if (splits[0].equalsIgnoreCase("Comment")) {
                comment = splits[1];
                continue;
            }
            if (splits[0].equalsIgnoreCase("Domain")) {
                domain = splits[1];
                continue;
            }
            if (splits[0].equalsIgnoreCase("Max-Age") && splits[1].matches("\\-?\\d+")) {
                maxage = Integer.valueOf(splits[1]);
                continue;
            }
            if (splits[0].equalsIgnoreCase("Path")) {
                path = splits[1];
                continue;
            }
            if (splits[0].equalsIgnoreCase("Version") && splits[1].matches("\\-?\\d+")) {
                version = Integer.valueOf(splits[1]);
                continue;
            }
            if (splits[0].equalsIgnoreCase("Time") && splits[1].matches("\\d+")) {
                time = Long.valueOf(splits[1]);
                continue;
            }
        }
    }

    public Cookie(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    public static void addRequestCookie(Header header, Cookie cookie) {
        List<String> values = header.get("Cookie");
        if (values == null || values.isEmpty()) header.put("Cookie", cookie.toRequestString());
        else {
            String value = values.get(0);
            if (value == null || value.trim().equals("")) header.put("Cookie", cookie.toRequestString());
            else header.put("Cookie", value + "; " + cookie.toRequestString());
        }
    }

    public static void addResponseCookie(Header header, Cookie cookie) {
        header.add("Set-Cookie", cookie.toResponseString());
    }

    public static void addRequestCookies(Header header, Cookie... cookies) {
        for (Cookie cookie : cookies) {
            addRequestCookie(header, cookie);
        }
    }

    public static void addResponseCookies(Header header, Cookie... cookies) {
        for (Cookie cookie : cookies) {
            addResponseCookie(header, cookie);
        }
    }

    public static Cookie getRequestCookie(Header header, String name) {
        Cookie[] cookies = getRequestCookies(header);
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    public static Cookie getResponseCookie(Header header, String name) {
        Cookie[] cookies = getResponseCookies(header);
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    public static Cookie[] getRequestCookies(Header header) {
        List<String> values = header.get("Cookie");
        if (values == null) return new Cookie[0];
        List<Cookie> cookies = new ArrayList<Cookie>();
        for (String value : values) {
            if (value.trim().equals("")) continue;
            String[] vs = value.split("\\s*;\\s*");
            for (String v : vs) {
                Cookie cookie = new Cookie(v);
                cookies.add(cookie);
            }
        }
        return cookies.toArray(new Cookie[cookies.size()]);
    }

    public static Cookie[] getResponseCookies(Header header) {
        List<String> values = header.get("Set-Cookie");
        if (values == null) return new Cookie[0];
        List<Cookie> cookies = new ArrayList<Cookie>();
        for (String v : values) {
            if (v.trim().equals("")) {
                continue;
            }
            Cookie cookie = new Cookie(v);
            cookies.add(cookie);
        }
        return cookies.toArray(new Cookie[cookies.size()]);
    }

    public String getName() {
        return name;
    }

    public Cookie setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Cookie setValue(String value) {
        this.value = value;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Cookie setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public Cookie setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public int getMaxage() {
        return maxage;
    }

    public Cookie setMaxage(int maxage) {
        this.maxage = maxage;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Cookie setPath(String path) {
        this.path = path;
        return this;
    }

    public boolean isSecure() {
        return secure;
    }

    public Cookie setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public Cookie setVersion(int version) {
        this.version = version;
        return this;
    }

    public long getTime() {
        return time;
    }

    public Cookie setTime(long time) {
        this.time = time;
        return this;
    }

    public boolean validate() {
        if (path == null || path.trim().equals("")) {
            return false;
        }
        if (name == null || name.trim().equals("")) {
            return false;
        }
        if (value == null || value.trim().equals("")) {
            return false;
        }
        if (maxage == 0) {
            return false;
        }
        return maxage <= 0 || System.currentTimeMillis() - time <= maxage * 1000;
    }

    public String toResponseString(boolean withtime) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value).append(";");
        if (comment != null) {
            sb.append("Conment").append("=").append(comment).append(";");
        }
        if (domain != null) {
            sb.append("Domain").append("=").append(domain).append(";");
        }
        if (path != null) {
            sb.append("Path").append("=").append(path).append(";");
        }
        if (secure) {
            sb.append("Secure").append(";");
        }
        sb.append("Max-Age").append("=").append(maxage).append(";");
        sb.append("Version").append("=").append(version).append(";");
        if (withtime) {
            sb.append("Time").append("=").append(time).append(";");
        }
        return sb.toString();
    }

    public String toResponseString() {
        return toResponseString(false);
    }

    public String toRequestString() {
        return name + "=" + value;
    }

}
