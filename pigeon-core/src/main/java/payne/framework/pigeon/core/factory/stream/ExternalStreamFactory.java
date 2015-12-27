package payne.framework.pigeon.core.factory.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import payne.framework.pigeon.core.toolkit.IOToolkit;

public class ExternalStreamFactory implements StreamFactory {
	private final File directory;

	public ExternalStreamFactory() throws IOException {
		this(System.getProperty("java.io.tmpdir"));
	}

	public ExternalStreamFactory(String directory) throws IOException {
		this(new File(directory));
	}

	public ExternalStreamFactory(File directory) throws IOException {
		int count = 0;
		while (!directory.exists() && count++ < 3) {
			directory.mkdirs();
		}
		if (!directory.exists()) {
			throw new IOException("make directory of path:" + directory.getAbsolutePath() + " fail with 3 times");
		}
		this.directory = directory;
	}

	public InputStream produce(OutputStream out) throws IOException {
		AdvancedFileOutputStream afos = (AdvancedFileOutputStream) out;
		return new AdvancedFileInputStream(afos.disposable);
	}

	public OutputStream produce() throws IOException {
		Disposable disposable = new Disposable(directory, UUID.randomUUID().toString() + ".tmp");
		return new AdvancedFileOutputStream(disposable);
	}

	public File getDirectory() {
		return directory;
	}

	private static class Disposable extends File {
		private static final long serialVersionUID = 9031208131073941097L;

		public Disposable(File parent, String child) {
			super(parent, child);
			this.deleteOnExit();
		}

		@Override
		protected void finalize() throws Throwable {
			this.delete();
			super.finalize();
		}

	}

	private static class AdvancedFileInputStream extends InputStream {
		private final Disposable disposable;
		private FileInputStream fileInputStream;

		public AdvancedFileInputStream(Disposable disposable) throws FileNotFoundException {
			this.disposable = disposable;
			this.fileInputStream = new FileInputStream(disposable);
		}

		@Override
		public synchronized void reset() throws IOException {
			IOToolkit.close(fileInputStream);
			fileInputStream = new FileInputStream(disposable);
		}

		@Override
		public int read() throws IOException {
			return fileInputStream.read();
		}

		@Override
		public int read(byte[] b) throws IOException {
			return fileInputStream.read(b);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return fileInputStream.read(b, off, len);
		}

		@Override
		public long skip(long n) throws IOException {
			return fileInputStream.skip(n);
		}

		@Override
		public int available() throws IOException {
			return fileInputStream.available();
		}

		@Override
		public void close() throws IOException {
			fileInputStream.close();
		}

	}

	private static class AdvancedFileOutputStream extends OutputStream {
		private final Disposable disposable;
		private FileOutputStream fileOutputStream;

		public AdvancedFileOutputStream(Disposable disposable) throws FileNotFoundException {
			this.disposable = disposable;
			this.fileOutputStream = new FileOutputStream(disposable);
		}

		@Override
		public void write(int b) throws IOException {
			fileOutputStream.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			fileOutputStream.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			fileOutputStream.write(b, off, len);
		}

		@Override
		public void flush() throws IOException {
			fileOutputStream.flush();
		}

		@Override
		public void close() throws IOException {
			fileOutputStream.close();
		}

	}

}
