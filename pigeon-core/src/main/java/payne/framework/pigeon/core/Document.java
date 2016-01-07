package payne.framework.pigeon.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URI;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.core.toolkit.InputStreamReadable;
import payne.framework.pigeon.core.toolkit.OutputStreamWritable;
import payne.framework.pigeon.core.toolkit.Readable;
import payne.framework.pigeon.core.toolkit.Writable;

public class Document implements FileWrapper, Externalizable {
	protected transient File file;
	protected String name;

	public Document() throws IOException {
		this.file = File.createTempFile(Document.class.getSimpleName(), ".tmp");
		this.name = file.getName();
	}

	public Document(File parent, String child) {
		this.file = new File(parent, child);
		this.name = file.getName();
	}

	public Document(String parent, String child) {
		this.file = new File(parent, child);
		this.name = file.getName();
	}

	public Document(String pathname) {
		this.file = new File(pathname);
		this.name = file.getName();
	}

	public Document(URI uri) {
		this.file = new File(uri);
		this.name = file.getName();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(file.getName());
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			IOToolkit.transmit(new InputStreamReadable(fis), new ObjectOutputWritable(out));
		} finally {
			IOToolkit.close(fis);
		}
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		name = in.readObject().toString();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			IOToolkit.transmit(new ObjectInputReadable(in), new OutputStreamWritable(fos));
		} finally {
			IOToolkit.close(fos);
		}
	}

	public void moveTo(File destination) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(file);
			fos = new FileOutputStream(destination);
			IOToolkit.transmit(fis, fos);
			file.delete();
			file = destination;
			name = destination.getName();
		} finally {
			IOToolkit.close(fis);
			IOToolkit.close(fos);
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() throws IOException {
		FileInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new FileInputStream(file);
			out = new ByteArrayOutputStream();
			IOToolkit.transmit(new Base64InputStream(in, true), out);
			return new String(out.toByteArray());
		} finally {
			IOToolkit.close(in);
			IOToolkit.close(out);
		}
	}

	public void setContent(String content) throws IOException {
		ByteArrayInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new ByteArrayInputStream(content.getBytes());
			out = new FileOutputStream(file);
			IOToolkit.transmit(in, new Base64OutputStream(out, false));
		} finally {
			IOToolkit.close(in);
			IOToolkit.close(out);
		}
	}

	@Override
	public String toString() {
		return "Document [file=" + file + ", name=" + name + "]";
	}

}

class ObjectOutputWritable implements Writable {
	private ObjectOutput output;

	public ObjectOutputWritable(ObjectOutput output) {
		super();
		this.output = output;
	}

	public int write(int b) throws IOException {
		return write(new byte[] { (byte) b });
	}

	public int write(byte[] b) throws IOException {
		return write(b, 0, b.length);
	}

	public int write(byte[] b, int off, int len) throws IOException {
		output.write(b, off, len);
		return len;
	}

	public ObjectOutput getOutput() {
		return output;
	}

}

class ObjectInputReadable implements Readable {
	private ObjectInput input;

	public ObjectInputReadable(ObjectInput input) {
		super();
		this.input = input;
	}

	public int read() throws IOException {
		return input.read();
	}

	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return input.read(b, off, len);
	}

	public ObjectInput getInput() {
		return input;
	}

}