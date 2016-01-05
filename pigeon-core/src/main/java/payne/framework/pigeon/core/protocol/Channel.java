package payne.framework.pigeon.core.protocol;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.List;

import payne.framework.pigeon.core.Attributed;
import payne.framework.pigeon.core.Constants;
import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.Timeoutable;
import payne.framework.pigeon.core.Transcoder;
import payne.framework.pigeon.core.annotation.Accept.Mode;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.processing.Step;
import payne.framework.pigeon.core.toolkit.Readable;
import payne.framework.pigeon.core.toolkit.Writable;

/**
 * 传输通道
 * 
 * @author yangchangpei
 * 
 */
public interface Channel extends Attributed, Transcoder, Readable, Writable, Closeable, Timeoutable, Constants {

	void initialize(String host, int port, String path, int timeout, String format) throws IOException;

	void initialize(Mode mode, String path, String parameter, String protocol, SocketAddress address, InputStream inputStream, OutputStream outputStream) throws IOException;

	void send(Invocation invocation, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception;

	Invocation receive(String expression, Method method, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception;

	void write(Invocation invocation, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception;

	Invocation read(String expression, Method method, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception;

	/**
	 * 获取服务端回应代码
	 * 
	 * @return
	 * @throws IOException
	 */
	State getStatus() throws IOException;

	/**
	 * 服务端回应状态码
	 * 
	 * @param status
	 * @throws IOException
	 */
	void setStatus(State status) throws IOException;

	Mode getMode();

	String getHost();

	int getPort();

	String getPath();

	String getParameter();

	String getProtocol();

	SocketAddress getAddress();

	Header getClientHeader();

	Header getServerHeader();

	int getReaded();

	int getWrited();

	boolean isConnected();

	boolean isAvailable();

	boolean isClosed();

	boolean isUsable();

}