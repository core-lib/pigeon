package org.qfox.detector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author Payne 646742615@qq.com
 *
 * @date 2016年1月16日 下午3:10:30
 *
 * @since 1.0.0
 */
public class JarEntryResource implements Resource {
	private final String name;
	private final URL url;
	private InputStream inputStream;
	private final Object lock = new Object();
	private final ClassLoader classLoader;

	public JarEntryResource(JarFile jarFile, JarEntry jarEntry, ClassLoader classLoader) throws IOException {
		super();
		this.name = jarEntry.getName().substring(jarEntry.getName().lastIndexOf('/') + 1);
		this.url = new URL("jar:file:" + jarFile.getName() + "!/" + jarEntry.getName());
		this.classLoader = classLoader;
	}

	public String getName() {
		return name;
	}

	public URL getUrl() {
		return url;
	}

	public InputStream getInputStream() throws IOException {
		if (inputStream != null) {
			return inputStream;
		}
		synchronized (lock) {
			if (inputStream != null) {
				return inputStream;
			}
			inputStream = newInputStream();
		}
		return inputStream;
	}

	public InputStream newInputStream() throws IOException {
		return url.openStream();
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public boolean isClass() {
		return url.getFile().endsWith(".class");
	}

	public Class<?> toClass() {
		if (isClass() == false) {
			throw new RuntimeException(url.toString() + " is not a class file");
		}
		String className = url.getFile().substring(url.getFile().lastIndexOf("!") + 2, url.getFile().lastIndexOf(".class"));
		className = className.replace('/', '.').replace('\\', '.');
		try {
			return classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * just close the input stream opened by method {@link JarEntryResource#getInputStream()}
	 */
	public void close() throws IOException {
		if (inputStream != null) {
			inputStream.close();
		}
	}

	@Override
	public String toString() {
		return url.toString();
	}

}
