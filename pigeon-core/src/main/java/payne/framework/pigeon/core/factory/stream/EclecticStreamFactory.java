package payne.framework.pigeon.core.factory.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.toolkit.IOToolkit;

/**
 * 折衷的IO流工厂,可以适应长数据和短数据转换传输,一开始先用内存交换,当数据量超过一个给定的阀值后将采用文件方式交换
 * 默认阀值为512KB,即数据量超过512KB时将采用文件形式进行数据交换
 * 
 * @author yangchangpei
 *
 */
public class EclecticStreamFactory implements StreamFactory {
	private final int threshold;
	private StreamFactory quickStreamFactory;
	private StreamFactory bulkyStreamFactory;

	public EclecticStreamFactory() throws IOException {
		this(512 * 1024);
	}

	public EclecticStreamFactory(int threshold) throws IOException {
		super();
		if (threshold < 0) {
			throw new IllegalArgumentException("threshold must not a negative value");
		}
		this.threshold = threshold;
		this.quickStreamFactory = new InternalStreamFactory();
		this.bulkyStreamFactory = new ExternalStreamFactory();
	}

	public InputStream produce(OutputStream out) throws IOException {
		CombinedOutputStream cos = (CombinedOutputStream) out;
		InputStream quickInputStream = quickStreamFactory.produce(cos.quickOutputStream);
		InputStream bulkyInputStream = bulkyStreamFactory.produce(cos.bulkyOutputStream);
		return new CombinedInputStream(threshold, quickInputStream, bulkyInputStream);
	}

	public OutputStream produce() throws IOException {
		OutputStream quickOutputStream = quickStreamFactory.produce();
		OutputStream bulkyOutputStream = bulkyStreamFactory.produce();
		return new CombinedOutputStream(threshold, quickOutputStream, bulkyOutputStream);
	}

	public int getThreshold() {
		return threshold;
	}

	public StreamFactory getQuickStreamFactory() {
		return quickStreamFactory;
	}

	public void setQuickStreamFactory(StreamFactory quickStreamFactory) {
		this.quickStreamFactory = quickStreamFactory;
	}

	public StreamFactory getBulkyStreamFactory() {
		return bulkyStreamFactory;
	}

	public void setBulkyStreamFactory(StreamFactory bulkyStreamFactory) {
		this.bulkyStreamFactory = bulkyStreamFactory;
	}

	public static class CombinedInputStream extends InputStream {
		private final int threshold;
		private final InputStream quickInputStream;
		private final InputStream bulkyInputStream;

		private long position;

		public CombinedInputStream(int threshold, InputStream quickInputStream, InputStream bulkyInputStream) {
			super();
			this.threshold = threshold;
			this.quickInputStream = quickInputStream;
			this.bulkyInputStream = bulkyInputStream;
		}

		@Override
		public int read() throws IOException {
			int b;
			if (position < threshold) {
				b = quickInputStream.read();
			} else {
				b = bulkyInputStream.read();
			}
			position++;
			return b;
		}

		@Override
		public int available() throws IOException {
			return quickInputStream.available() + bulkyInputStream.available();
		}

		@Override
		public synchronized void reset() throws IOException {
			quickInputStream.reset();
			bulkyInputStream.reset();
			position = 0;
		}

		@Override
		public long skip(long n) throws IOException {
			if (n < threshold) {
				position = n;
				return quickInputStream.skip(position);
			} else {
				quickInputStream.skip(quickInputStream.available());
				position = n - threshold;
				return bulkyInputStream.skip(position);
			}
		}

		@Override
		public void close() throws IOException {
			IOToolkit.close(quickInputStream);
			IOToolkit.close(bulkyInputStream);
		}

	}

	public static class CombinedOutputStream extends OutputStream {
		private final int threshold;
		private final OutputStream quickOutputStream;
		private final OutputStream bulkyOutputStream;

		private long position;

		public CombinedOutputStream(int threshold, OutputStream quickOutputStream, OutputStream bulkyOutputStream) {
			super();
			this.threshold = threshold;
			this.quickOutputStream = quickOutputStream;
			this.bulkyOutputStream = bulkyOutputStream;
		}

		@Override
		public void write(int b) throws IOException {
			if (position < threshold) {
				quickOutputStream.write(b);
			} else {
				bulkyOutputStream.write(b);
			}
			position++;
		}

		@Override
		public void flush() throws IOException {
			quickOutputStream.flush();
			bulkyOutputStream.flush();
		}

		@Override
		public void close() throws IOException {
			IOToolkit.close(quickOutputStream);
			IOToolkit.close(bulkyOutputStream);
		}

	}

}
