package payne.framework.pigeon.core.detector;

import java.io.File;
import java.io.IOException;
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
 * @date 2016年1月8日 上午11:25:53
 *
 * @version 1.0.0
 */
public class SimpleResourceDetector implements ResourceDetector {
	private final String root;
	private final boolean recursive;
	private final ClassLoader classLoader;

	public SimpleResourceDetector() {
		this("/");
	}

	public SimpleResourceDetector(String root) {
		this(root, true);
	}

	public SimpleResourceDetector(String root, boolean recursive) {
		this(root, recursive, Thread.currentThread().getContextClassLoader());
	}

	public SimpleResourceDetector(String root, boolean recursive, ClassLoader classLoader) {
		super();
		this.root = root.replaceAll("\\.+", "/");
		this.recursive = recursive;
		this.classLoader = classLoader;
	}

	public Set<Resource> detect(ResourceFilter... filters) throws DetectorException {
		try {
			Set<Resource> resources = new LinkedHashSet<Resource>();
			Enumeration<URL> enumeration = classLoader.getResources(root);
			while (enumeration != null && enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();
				// 如果是文件
				if ("file".equals(url.getProtocol())) {
					resources.addAll(detect(new File(url.getFile()), filters));
				}
				// 如果是jar包
				else if ("jar".equals(url.getProtocol())) {
					String file = url.getFile();
					String path = file.substring(file.indexOf(":") + 1, file.lastIndexOf("!"));
					JarFile jar = null;
					try {
						jar = new JarFile(path);
						resources.addAll(detect(jar, filters));
					} finally {
						jar.close();
					}
				}
				// 否则不做处理
				else {
					continue;
				}
			}
			return resources;
		} catch (IOException e) {
			throw new DetectorException(e);
		}

	}

	protected Set<? extends Resource> detect(final File file, ResourceFilter... filters) throws IOException {
		Set<Resource> resources = new LinkedHashSet<Resource>();
		// 如果是目录而且递归查找
		if (file.isDirectory() && recursive) {
			for (File subfile : file.listFiles()) {
				resources.addAll(detect(subfile, filters));
			}
		}
		// 如果是文件
		else if (file.isFile()) {
			Resource resource = new DiskFileResource(file);
			for (ResourceFilter filter : filters) {
				if (filter.accept(resource) == false) {
					return resources;
				}
			}
			resources.add(resource);
		}
		return resources;
	}

	protected Set<? extends Resource> detect(JarFile jar, ResourceFilter... filters) {
		Set<Resource> resources = new LinkedHashSet<Resource>();
		Enumeration<JarEntry> entries = jar.entries();
		flag: while (entries != null && entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			Resource resource = new JarEntryResource(jar, entry);
			// 过滤,只有通过所有过滤器的筛选才算通过
			for (ResourceFilter filter : filters) {
				if (filter.accept(resource) == false) {
					continue flag;
				}
			}
			resources.add(resource);
		}
		return resources;
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
