package payne.framework.pigeon.core.dynamic;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import payne.framework.pigeon.core.exception.DynamicInvocationArchiveException;
import payne.framework.pigeon.core.toolkit.IOToolkit;

public class PlainDynamicInvocationArchiver implements DynamicInvocationArchiver {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String directory;

	public PlainDynamicInvocationArchiver() {
		super();
		this.directory = System.getProperty("java.io.tmpdir");
	}

	public PlainDynamicInvocationArchiver(String directory) {
		super();
		this.directory = directory;
	}

	public void archive(String name, Set<Generation> generations) throws DynamicInvocationArchiveException {
		logger.info("start archive dynamic invocation classes to temporary directory {}", directory);
		File parent = new File(directory);
		FileOutputStream fos = null;
		try {
			if (!parent.exists()) {
				parent.mkdirs();
			}
			for (Generation generation : generations) {
				logger.info("archiving {}.class", generation.getName().replaceAll("\\.", "/"));
				File file = new File(parent, generation.getName().replaceAll("\\.", "/") + ".class");
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				fos = new FileOutputStream(file);
				fos.write(generation.getBytes());
				IOToolkit.close(fos);
			}
			DynamicInvocationClassLoader.getInstance().addURL(parent.toURI().toURL());
			logger.info("archive completed");
		} catch (Exception e) {
			logger.error("archiving classes {} to directory {} fail", generations, parent, e);
			throw new DynamicInvocationArchiveException(e, generations);
		} finally {
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
