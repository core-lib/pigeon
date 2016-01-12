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
import java.util.Map;
import java.util.Map.Entry;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.Path;
import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.annotation.Accept.Mode;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.formatting.FormatInvocationInputStream;
import payne.framework.pigeon.core.formatting.FormatInvocationOutputStream;
import payne.framework.pigeon.core.formatting.InvocationFormatter;
import payne.framework.pigeon.core.formatting.Structure;
import payne.framework.pigeon.core.formatting.URLInvocationFormatter;
import payne.framework.pigeon.core.processing.InvocationFormatProcedure;
import payne.framework.pigeon.core.processing.Step;
import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.core.toolkit.ReadableInputStream;
import payne.framework.pigeon.core.toolkit.WritableOutputStream;

public class HTTPChannel extends TransferableChannel implements Chunkable {
	protected String protocol;
	protected String host;
	protected int port;
	protected Mode mode;
	protected String file;
	protected String parameter;
	protected SocketAddress address;
	protected HttpURLConnection connection;

	protected Header clientHeader;
	protected Header serverHeader;

	protected boolean connected;
	protected boolean closed;
	protected int chunksize = -1;

	public void initialize(String host, int port, Mode mode, String file, int timeout, String format) throws IOException {
		this.protocol = "HTTP";
		this.host = host;
		this.port = port;
		this.mode = mode;
		this.file = file;
		this.parameter = "";
		this.address = new InetSocketAddress(host, port);
		this.connection = (HttpURLConnection) new URL(protocol, host, port, file).openConnection();
		this.connection.setDoOutput(true);
		this.connection.setDoInput(true);
		this.connection.setUseCaches(false);
		this.connection.setRequestMethod(mode.name());
		this.connection.setConnectTimeout(timeout);
		this.connected = true;
		this.closed = false;
	}

	public void initialize(String protocol, Mode mode, String file, String parameter, SocketAddress address, InputStream inputStream, OutputStream outputStream) throws IOException {
		this.protocol = protocol;
		this.mode = mode;
		this.file = file;
		this.parameter = parameter;
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

	public void send(Path path, Invocation invocation, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception {
		OutputStream wrap = null;
		try {
			clientHeader = invocation.getClientHeader();
			wrap = new WritableOutputStream(this);

			InvocationFormatter formatter = beanFactory.get(clientHeader.getContentType(), InvocationFormatter.class);
			Method method = invocation.getMethod();
			InvocationFormatProcedure procedure = new InvocationFormatProcedure(formatter, Structure.forArray(method.getGenericParameterTypes(), method.getParameterAnnotations()));
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

	public Invocation receive(Path path, Method method, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception {
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
			InvocationFormatProcedure procedure = new InvocationFormatProcedure(formatter, Structure.forValue(method.getGenericReturnType(), method.getAnnotations()));
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

	public void write(Path path, Invocation invocation, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception {
		serverHeader = invocation.getServerHeader();
		String contentType = invocation.getClientHeader().getContentType();
		serverHeader.setContentType(contentType != null && contentType.trim().length() > 0 ? contentType.trim() : "application/json");
		if ("chunked".equalsIgnoreCase(clientHeader.getTransferEncoding())) {
			serverHeader.setTransferEncoding("chunked");
			new ChunkedInvocationWriter(this, serverHeader).write(invocation, beanFactory, streamFactory, steps);
		} else {
			new FixedLengthInvocationWriter(this, serverHeader).write(invocation, beanFactory, streamFactory, steps);
		}
	}

	public Invocation read(Path path, Method method, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception {
		Invocation invocation = null;
		// 如果没有body的则要解析路径参数了查询参数
		if (this.mode.bodied == false) {
			Map<String, List<String>> arguments = Pigeons.getPathArguments(path, file, method);
			String query = Pigeons.getQueryString(arguments);
			query += parameter != null && parameter.trim().length() > 0 ? "&" + parameter.trim() : "";
			InputStream in = new ByteArrayInputStream(query.getBytes());
			InvocationFormatter formatter = new URLInvocationFormatter();
			Object data = formatter.deserialize(Structure.forArray(method.getGenericParameterTypes(), method.getParameterAnnotations()), in, charset);
			invocation = new Invocation();
			invocation.setArguments(data == null ? new Object[method.getParameterTypes().length] : (Object[]) data);
		} else if ("chunked".equalsIgnoreCase(clientHeader.getTransferEncoding())) {
			invocation = new ChunkedInvocationReader(this, clientHeader).read(method, beanFactory, streamFactory, steps);
		} else {
			invocation = new FixedLengthInvocationReader(this, clientHeader).read(method, beanFactory, streamFactory, steps);
		}
		invocation.setClientHeader(clientHeader);
		invocation.setFile(this.file);
		return invocation;
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

	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public Mode getMode() {
		return mode;
	}

	public String getFile() {
		return file;
	}

	public String getParameter() {
		return parameter;
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
