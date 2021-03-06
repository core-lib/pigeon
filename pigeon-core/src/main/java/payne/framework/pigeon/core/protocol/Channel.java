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
import payne.framework.pigeon.core.Path;
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

	/**
	 * 客户端初始化传输通道方法
	 * 
	 * @param host
	 *            主机名
	 * @param port
	 *            服务端口
	 * @param mode
	 *            请求方法
	 * @param file
	 *            文件/方法
	 * @param timeout
	 *            连接超时
	 * @param format
	 *            数据格式
	 * @throws IOException
	 */
	void initialize(String host, int port, Mode mode, String file, int timeout, String format) throws IOException;

	/**
	 * 服务端初始化传输通道方法
	 * 
	 * @param protocol
	 *            协议
	 * 
	 * @param mode
	 *            请求方法
	 * @param file
	 *            文件/方法
	 * @param parameter
	 *            参数
	 * @param address
	 *            客户端地址
	 * @param inputStream
	 *            输入流
	 * @param outputStream
	 *            输出流
	 * @throws IOException
	 */
	void initialize(String protocol, Mode mode, String file, String parameter, SocketAddress address, InputStream inputStream, OutputStream outputStream) throws IOException;

	void send(Path path, Invocation invocation, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception;

	Invocation receive(Path path, Method method, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception;

	void write(Path path, Invocation invocation, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception;

	Invocation read(Path path, Method method, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception;

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

	String getProtocol();

	String getHost();

	int getPort();

	Mode getMode();

	String getFile();

	String getParameter();

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