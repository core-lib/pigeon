package org.qfox.detector;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * <p>
 * Description: Normal disk file as a resource
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author Payne 646742615@qq.com
 *
 * @date 2016年1月16日 下午3:08:23
 *
 * @since 1.0.0
 */
public class DiskFileResource implements Resource {
	private final String name;
	private final URL url;
	private InputStream inputStream;
	private final Object lock = new Object();
	private final ClassLoader classLoader;

	public DiskFileResource(File file, ClassLoader classLoader) throws IOException {
		if (file == null || classLoader == null) {
			throw new NullPointerException();
		}
		this.name = file.getName();
		this.url = file.toURI().toURL();
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
		try {
			Enumeration<URL> enumeration = classLoader.getResources("");
			while (enumeration != null && enumeration.hasMoreElements()) {
				String classpath = enumeration.nextElement().getFile();
				if (url.getFile().startsWith(classpath)) {
					String className = url.getFile().substring(classpath.length() + (classpath.endsWith("/") || classpath.endsWith("\\") ? 0 : 1));
					className = className.substring(0, className.lastIndexOf(".class"));
					className = className.replace('/', '.').replace('\\', '.');
					return classLoader.loadClass(className);
				}
			}
			throw new ClassNotFoundException(url.getFile());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * only close the input stream opened by method {@link DiskFileResource#getInputStream()}
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
