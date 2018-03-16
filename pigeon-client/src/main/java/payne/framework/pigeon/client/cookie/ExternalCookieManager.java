package payne.framework.pigeon.client.cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import payne.framework.pigeon.core.Constants;
import payne.framework.pigeon.core.Cookie;
import payne.framework.pigeon.core.toolkit.IOToolkit;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件cookie管理器,实现通过cookie的domain属性进行文件管理,同一个domain的cookie保存在同一个文件,<br/>
 * 命名采用 domain + ".cookie"的形式,查找cookie时通过domain和path两个坐标去确定一个或多个cookie并返回
 *
 * @author yangchangpei
 */
public class ExternalCookieManager implements CookieManager, Constants {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String suffix = ".cookie";

    private String directory = System.getProperty("java.io.tmpdir");

    public ExternalCookieManager() {
        super();
    }

    public ExternalCookieManager(String directory) {
        super();
        this.directory = directory;
    }

    public void save(Cookie... cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.validate() == false) {
                continue;
            }
            // 找到和该cookie应当放置的文件的所有cookies
            Cookie[] cks = find(cookie.getDomain(), null);
            // 覆盖相同name的cookie
            Map<String, Cookie> map = new HashMap<String, Cookie>();
            for (int i = 0; cks != null && i < cks.length; i++) {
                Cookie ck = cks[i];
                map.put(ck.getName(), ck);
            }
            map.put(cookie.getName(), cookie);
            // 写回文件中 如果domain里面包含端口信息 那么用 '_' 去替代 ':'
            File file = new File(directory, cookie.getDomain().replace(':', '_') + suffix);
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                logger.warn("could not create directory: " + file.getParentFile());
                return;
            }
            FileOutputStream fos = null;
            PrintWriter pw = null;
            try {
                fos = new FileOutputStream(file);
                pw = new PrintWriter(fos, true);
                for (Cookie ck : map.values()) {
                    pw.println(ck.toResponseString(true));
                }
                pw.flush();
            } catch (Exception e) {
                logger.warn("saving cookie {} fail", cookie);
            } finally {
                IOToolkit.close(pw);
                IOToolkit.close(fos);
            }
        }
    }

    public Cookie[] find(String domain, String path) {
        if (domain == null) {
            return null;
        }
        // 根据domain寻找对应文件
        File file = new File(directory, domain.replace(':', '_') + suffix);
        // 如果文件还不存在那么 返回 长度为0的数组
        if (!file.exists()) {
            return null;
        }
        // 如果存在 将逐行读取文件 一行代表一个cookie 并且通过 path 筛选匹配的cookie
        List<Cookie> cookies = new ArrayList<Cookie>();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                Cookie cookie = new Cookie(line);
                // 忽略过期的
                if (cookie.getMaxage() >= 0 && System.currentTimeMillis() - cookie.getTime() > cookie.getMaxage() * 1000) {
                    continue;
                }
                // 选择路径匹配的
                if (path == null || (cookie.getPath() != null && path.startsWith(cookie.getPath()))) {
                    cookies.add(cookie);
                }
            }
        } catch (Exception e) {
            logger.warn("reading cookies for domain {} and path {} fail", domain, path);
        } finally {
            IOToolkit.close(br);
            IOToolkit.close(isr);
            IOToolkit.close(fis);
        }
        return cookies.toArray(new Cookie[cookies.size()]);
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

}
