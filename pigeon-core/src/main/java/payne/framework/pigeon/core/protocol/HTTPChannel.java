package payne.framework.pigeon.core.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.annotation.Accept.Mode;
import payne.framework.pigeon.core.annotation.Param;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.formatting.FormatInvocationInputStream;
import payne.framework.pigeon.core.formatting.FormatInvocationOutputStream;
import payne.framework.pigeon.core.formatting.InvocationFormatter;
import payne.framework.pigeon.core.formatting.Structure;
import payne.framework.pigeon.core.processing.InvocationFormatProcedure;
import payne.framework.pigeon.core.processing.Step;
import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.core.toolkit.ReadableInputStream;
import payne.framework.pigeon.core.toolkit.WritableOutputStream;

public class HTTPChannel extends TransferableChannel implements Chunkable {
	protected static Pattern pattern = Pattern.compile("\\{(?:(\\w+)\\:)?(.*?)\\}");

	protected Mode mode;
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
		this.mode = Mode.POST;
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

	public void initialize(Mode mode, String path, String parameter, String protocol, SocketAddress address, InputStream inputStream, OutputStream outputStream) throws IOException {
		this.mode = mode;
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

	public Invocation receive(String expression, Method method, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception {
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

	public void write(Invocation invocation, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception {
		serverHeader = invocation.getServerHeader();
		String contentType = invocation.getClientHeader().getContentType();
		serverHeader.setContentType(contentType != null && contentType.trim().length() > 0 ? contentType : "application/json");
		if ("chunked".equalsIgnoreCase(clientHeader.getTransferEncoding())) {
			serverHeader.setTransferEncoding("chunked");
			new ChunkedInvocationWriter(this, serverHeader).write(invocation, beanFactory, streamFactory, steps);
		} else {
			new FixedLengthInvocationWriter(this, serverHeader).write(invocation, beanFactory, streamFactory, steps);
		}
	}

	public Invocation read(String expression, Method method, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception {
		if (this.mode == Mode.GET) {
			// 处理路径变量和查询参数问题,将路径变量拿出来作为请求参数的一部分交给解析器解析
			List<String> variables = new ArrayList<String>();
			Matcher matcher = pattern.matcher(expression);
			String regex = expression;
			while (matcher.find()) {
				String name = matcher.group(1);
				String regular = matcher.group(2);
				// 如果group(1) == null 的话其实整个都是名称 例如 /{page}/{size} 所以应该匹配所有字符
				regex = regex.replace(matcher.group(), "(" + (name != null ? regular : "[^/]*") + ")");
				variables.add(name != null ? name : regular);
			}

			String query = parameter;

			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(path);
			if (m.find()) {
				for (int i = 1; i <= m.groupCount(); i++) {
					String name = variables.get(i - 1);
					String value = m.group(i);
					if (name == null || name.trim().length() == 0) {
						continue;
					}
					name = name.trim();
					value = value.trim();
					// 如果是数字的话可能是下标也可能是名称
					if (name.matches("\\d+")) {
						// 先寻找一遍有没有这个名称
						boolean found = false;
						flag: for (Annotation[] annotations : method.getParameterAnnotations()) {
							for (Annotation annotation : annotations) {
								if (annotation instanceof Param && ((Param) annotation).value().trim().equals(name)) {
									found = true;
									break flag;
								}
							}
						}
						// 如果没有找到则看下该下标的参数是否有指定名称
						if (found == false) {
							int index = Integer.valueOf(name) - 1;
							name = "argument" + index;
							for (Annotation annotation : method.getParameterAnnotations().length > index ? method.getParameterAnnotations()[index] : new Annotation[0]) {
								// 如果有而且指定名称不是默认值
								if (annotation instanceof Param && ((Param) annotation).value().trim().length() > 0) {
									name = ((Param) annotation).value().trim();
									break;
								}
							}
						}
					}
					query = query + (query.trim().length() > 0 ? "&" : "") + name + "=" + value;
				}
			}

			inputStream = new ByteArrayInputStream(query.getBytes());
			clientHeader.setContentLength(inputStream.available());
		}
		Invocation invocation = null;
		if ("chunked".equalsIgnoreCase(clientHeader.getTransferEncoding())) {
			invocation = new ChunkedInvocationReader(this, clientHeader).read(method, beanFactory, streamFactory, steps);
		} else {
			invocation = new FixedLengthInvocationReader(this, clientHeader).read(method, beanFactory, streamFactory, steps);
		}
		invocation.setPath(path);
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

	public Mode getMode() {
		return mode;
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
