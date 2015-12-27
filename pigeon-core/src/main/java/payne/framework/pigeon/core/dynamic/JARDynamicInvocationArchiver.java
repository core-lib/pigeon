package payne.framework.pigeon.core.dynamic;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import payne.framework.pigeon.core.exception.DynamicInvocationArchiveException;
import payne.framework.pigeon.core.toolkit.IOToolkit;

public class JARDynamicInvocationArchiver implements DynamicInvocationArchiver {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String directory;

	public JARDynamicInvocationArchiver() {
		super();
		this.directory = System.getProperty("java.io.tmpdir");
	}

	public JARDynamicInvocationArchiver(String directory) {
		super();
		this.directory = directory;
	}

	public void archive(String name, Set<Generation> generations) throws DynamicInvocationArchiveException {
		FileOutputStream fos = null;
		JarOutputStream jos = null;
		File file = new File(directory + "/" + name + ".jar");
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			logger.info("archiving {}.jar to temporary directory {}", name, directory);
			fos = new FileOutputStream(file);
			jos = new JarOutputStream(fos);
			for (Generation generation : generations) {
				logger.info("including class {}", generation.getName());
				jos.putNextEntry(new ZipEntry(generation.getName().replaceAll("\\.", "/") + ".class"));
				jos.write(generation.getBytes());
				jos.closeEntry();
			}
			DynamicInvocationClassLoader.getInstance().addURL(file.toURI().toURL());
			logger.info("archive completed");
		} catch (Exception e) {
			logger.error("archiving classes {} to {} fail", generations, file, e);
			throw new DynamicInvocationArchiveException(e, generations);
		} finally {
			IOToolkit.close(jos);
			IOToolkit.close(fos);
		}
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

}
