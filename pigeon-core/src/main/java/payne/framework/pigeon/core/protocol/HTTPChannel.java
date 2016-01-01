package payne.framework.pigeon.core.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.formatting.FormatInvocationInputStream;
import payne.framework.pigeon.core.formatting.FormatInvocationOutputStream;
import payne.framework.pigeon.core.formatting.InvocationFormatter;
import payne.framework.pigeon.core.processing.InvocationFormatProcedure;
import payne.framework.pigeon.core.processing.Step;
import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.core.toolkit.ReadableInputStream;
import payne.framework.pigeon.core.toolkit.WritableOutputStream;

public class HTTPChannel extends TransferableChannel implements Chunkable {
	protected String method;
	protected String host;
	protected int port;
	protected String path;
	protected String parameter;
	protected String protocol;
	protected SocketAddress address;
	protected HttpURLConnection connection;

	protected Header clientHeader;
	protected Header serverHeader;

	protected boolean connected;
	protected boolean closed;
	protected int chunksize = -1;

	public void initialize(String host, int port, String path, int timeout, String format) throws IOException {
		this.method = "POST";
		this.host = host;
		this.port = port;
		this.path = path;
		this.parameter = "";
		this.protocol = "HTTP";
		this.address = new InetSocketAddress(host, port);
		this.connection = (HttpURLConnection) new URL("http", host, port, path).openConnection();
		this.connection.setDoOutput(true);
		this.connection.setDoInput(true);
		this.connection.setUseCaches(false);
		this.connection.setRequestMethod("POST");
		this.connection.setConnectTimeout(timeout);
		this.connected = true;
		this.closed = false;
	}

	public void initialize(String method, String path, String parameter, String protocol, SocketAddress address, InputStream inputStream, OutputStream outputStream) throws IOException {
		this.method = method;
		this.path = path;
		this.parameter = parameter;
		this.protocol = protocol;
		this.address = address;
		if (address instanceof InetSocketAddress) {
			InetSocketAddress isa = (InetSocketAddress) address;
			this.host = isa.getAddress().getHostAddress();
			this.port = isa.getPort();
		} else {
			this.host = null;
			this.port = -1;
		}
		this.inputStream = inputStream;
		this.outputStream = outputStream;

		this.clientHeader = new Header();
		String line = null;
		int index = -1;
		while ((line = IOToolkit.readLine(this)) != null && !line.trim().equals("")) {
			index = line.indexOf(':');
			if (index == -1) {
				continue;
			}
			this.clientHeader.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
		}

		this.connected = true;
		this.closed = false;
	}

	public void send(Invocation invocation, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception {
		OutputStream wrap = null;
		try {
			clientHeader = invocation.getClientHeader();
			wrap = new WritableOutputStream(this);

			InvocationFormatter formatter = beanFactory.get(clientHeader.getContentType(), InvocationFormatter.class);
			InvocationFormatProcedure procedure = new InvocationFormatProcedure(formatter, invocation);
			Step step = new Step(null, null, procedure);
			steps.add(0, step);

			for (int i = 0; i < steps.size(); i++) {
				Step s = steps.get(i);
				s.initialize(SIDE_CLIENT, beanFactory, streamFactory, this, clientHeader);
			}

			if (chunksize > 0) {
				this.connection.setChunkedStreamingMode(chunksize);
			}
			for (Entry<String, String> entry : clientHeader.entrySet()) {
				this.connection.addRequestProperty(entry.getKey(), entry.getValue());
			}

			this.connection.connect();
			this.outputStream = this.connection.getOutputStream();

			for (int i = steps.size() - 1; i >= 0; i--) {
				Step s = steps.get(i);
				wrap = s.wrap(SIDE_CLIENT, beanFactory, streamFactory, this, clientHeader, wrap);
			}

			FormatInvocationOutputStream fios = (FormatInvocationOutputStream) wrap;
			fios.serialize(invocation.getArguments());
			fios.flush();

			this.inputStream = connection.getResponseCode() == HttpURLConnection.HTTP_OK ? connection.getInputStream() : connection.getErrorStream();
		} finally {
			IOToolkit.close(wrap);
		}
	}

	public Invocation receive(Method method, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception {
		InputStream wrap = null;
		try {
			serverHeader = new Header();
			for (String name : connection.getHeaderFields().keySet()) {
				String value = connection.getHeaderField(name);
				if (name == null || value == null) {
					continue;
				}
				serverHeader.put(name, value);
			}

			wrap = new ReadableInputStream(this);

			InvocationFormatter formatter = beanFactory.get(serverHeader.getContentType(), InvocationFormatter.class);
			InvocationFormatProcedure procedure = new InvocationFormatProcedure(formatter, method);
			Step step = new Step(null, null, procedure);
			steps.add(0, step);

			for (int i = steps.size() - 1; i >= 0; i--) {
				Step s = steps.get(i);
				wrap = s.wrap(SIDE_CLIENT, beanFactory, streamFactory, this, serverHeader, wrap);
			}

			FormatInvocationInputStream fiis = (FormatInvocationInputStream) wrap;
			Invocation invocation = new Invocation();
			Object data = fiis.deserialize();
			invocation.setResult(data);
			invocation.setServerHeader(serverHeader);
			return invocation;
		} finally {
			IOToolkit.close(wrap);
		}
	}

	public void write(Invocation invocation, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception {
		serverHeader = invocation.getServerHeader();
		serverHeader.setContentType(invocation.getClientHeader().getContentType());
		if ("chunked".equalsIgnoreCase(clientHeader.getTransferEncoding())) {
			serverHeader.setTransferEncoding("chunked");
			new ChunkedInvocationWriter(this, serverHeader).write(invocation, beanFactory, streamFactory, steps);
		} else {
			new FixedLengthInvocationWriter(this, serverHeader).write(invocation, beanFactory, streamFactory, steps);
		}
	}

	public Invocation read(Method method, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception {
		if ("GET".equalsIgnoreCase(this.method)) {
			inputStream = new ByteArrayInputStream(parameter.getBytes());
			clientHeader.setContentLength(inputStream.available());
		}
		if ("chunked".equalsIgnoreCase(clientHeader.getTransferEncoding())) {
			return new ChunkedInvocationReader(this, clientHeader).read(method, beanFactory, streamFactory, steps);
		} else {
			return new FixedLengthInvocationReader(this, clientHeader).read(method, beanFactory, streamFactory, steps);
		}
	}

	public State getStatus() throws IOException {
		int code = this.connection.getResponseCode();
		String message = this.connection.getResponseMessage();
		return new State(code, message);
	}

	public void setStatus(State state) throws IOException {
		if (this.state != null) {
			throw new IOException("write duplicate response head");
		}
		IOToolkit.writeLine(protocol + " " + state.toString(), this);
		this.state = state;
	}

	public String getMethod() {
		return method;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getPath() {
		return path;
	}

	public String getParameter() {
		return parameter;
	}

	public String getProtocol() {
		return protocol;
	}

	public SocketAddress getAddress() {
		return address;
	}

	public Header getClientHeader() {
		return clientHeader;
	}

	public Header getServerHeader() {
		return serverHeader;
	}

	public boolean isConnected() {
		return connected;
	}

	public boolean isAvailable() {
		return connected && !closed;
	}

	public boolean isClosed() {
		return closed;
	}

	public boolean isUsable() {
		return !connected && closed;
	}

	public int getChunksize() {
		return chunksize;
	}

	public void setChunksize(int chunksize) {
		this.chunksize = chunksize;
	}

	@Override
	public void close() throws IOException {
		super.close();
		IOToolkit.close(connection);
		this.closed = true;
		this.connected = false;
	}

}
