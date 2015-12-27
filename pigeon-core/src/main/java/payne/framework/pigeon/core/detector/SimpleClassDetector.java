package payne.framework.pigeon.core.detector;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
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
 * @author yangchangpei 646742615@qq.com
 *
 * @date 2015年11月8日 下午6:21:33
 *
 * @version 1.0.0
 */
public class SimpleClassDetector implements ClassDetector {
	private final String root;
	private final boolean recursive;
	private final ClassLoader classLoader;

	public SimpleClassDetector() {
		this("/");
	}

	public SimpleClassDetector(String root) {
		this(root, true);
	}

	public SimpleClassDetector(String root, boolean recursive) {
		this(root, recursive, Thread.currentThread().getContextClassLoader());
	}

	public SimpleClassDetector(String root, boolean recursive, ClassLoader classLoader) {
		super();
		this.root = root.replaceAll("\\.+", "/");
		this.recursive = recursive;
		this.classLoader = classLoader;
	}

	public Set<Class<?>> detect(ClassFilter... filters) throws ClassDetectorException {
		try {
			Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
			Enumeration<URL> enumeration = classLoader.getResources(root);
			while (enumeration != null && enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();
				// 如果是文件
				if ("file".equals(url.getProtocol())) {
					classes.addAll(detect(new File(url.getFile()), filters));
				}
				// 如果是jar包
				else if ("jar".equals(url.getProtocol())) {
					String file = url.getFile();
					String path = file.substring(file.indexOf(":") + 1, file.lastIndexOf("!"));
					JarFile jar = null;
					try {
						jar = new JarFile(path);
						classes.addAll(detect(jar, filters));
					} finally {
						jar.close();
					}
				}
				// 否则不做处理
				else {
					continue;
				}
			}
			return classes;
		} catch (Exception e) {
			throw new ClassDetectorException(e);
		}
	}

	protected Set<Class<?>> detect(File file, ClassFilter... filters) throws ClassNotFoundException {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		// 如果是目录而且递归查找
		if (file.isDirectory() && recursive) {
			for (File subfile : file.listFiles()) {
				classes.addAll(detect(subfile, filters));
			}
		}
		// 如果是文件而且以.class结尾
		else if (file.isFile() && file.getName().endsWith(".class")) {
			String path = file.getPath();
			// 转换成类名
			String className = path.substring(path.indexOf(root), path.lastIndexOf(".class")).replaceAll("\\/+", ".");
			// 加载字节码
			Class<?> clazz = classLoader.loadClass(className);
			// 过滤,只有通过所有过滤器的筛选才算通过
			for (ClassFilter filter : filters) {
				if (!filter.accept(clazz)) {
					return classes;
				}
			}
			classes.add(clazz);
		}
		return classes;
	}

	protected Set<Class<?>> detect(JarFile jar, ClassFilter... filters) throws ClassNotFoundException {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		Enumeration<JarEntry> entries = jar.entries();
		flag: while (entries != null && entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			if (!name.endsWith(".class")) {
				continue;
			}
			String className = name.replaceAll("\\/+", ".").substring(0, name.lastIndexOf(".class"));
			Class<?> clazz = classLoader.loadClass(className);
			// 过滤,只有通过所有过滤器的筛选才算通过
			for (ClassFilter filter : filters) {
				if (!filter.accept(clazz)) {
					continue flag;
				}
			}
			classes.add(clazz);
		}
		return classes;
	}

	public String getRoot() {
		return root;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

}
