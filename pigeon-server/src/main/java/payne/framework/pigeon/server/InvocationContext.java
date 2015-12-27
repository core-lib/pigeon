package payne.framework.pigeon.server;

import java.net.SocketAddress;

import payne.framework.pigeon.core.Attributed;
import payne.framework.pigeon.core.Transcoder;
import payne.framework.pigeon.core.exception.UnmappedPathException;
import payne.framework.pigeon.core.filtration.FilterManager;
import payne.framework.pigeon.core.protocol.Channel;
import payne.framework.pigeon.server.exception.ContextStartupException;
import payne.framework.pigeon.server.exception.DuplicatePathException;
import payne.framework.pigeon.server.exception.InvalidPathException;
import payne.framework.pigeon.server.exception.UnregulatedInterfaceException;

/**
 * 开放接口容器
 * 
 * @author ron
 * 
 */
public interface InvocationContext extends ConfigurableInvocationContext, Attributed, Transcoder, FilterManager<Channel, InvocationContext> {

	/**
	 * 将服务绑定到本机端口
	 * 
	 * @param port
	 *            本机端口
	 */
	void bind(int port);

	/**
	 * 将服务绑定到指定地址
	 * 
	 * @param address
	 *            指定地址
	 */
	void bind(SocketAddress address);

	/**
	 * 启动
	 * 
	 * @throws ContextStartupException
	 *             启动异常
	 */
	void startup() throws ContextStartupException;

	/**
	 * 关闭
	 */
	void shutdown();

	/**
	 * 状态
	 * 
	 * @return 状态
	 */
	Status status();

	/**
	 * 判断路径是否存在
	 * 
	 * @param path
	 *            路径
	 * @return 如果存在:true 否则:false
	 */
	boolean exists(String path);

	/**
	 * 获取路径对应的处理器
	 * 
	 * @param path
	 *            路径
	 * @return 指定路径的对应处理器
	 * @throws UnmappedPathException
	 *             如果路径不存在则抛出该异常
	 */
	InvocationProcessor lookup(String path) throws UnmappedPathException;

	/**
	 * 注册开放接口api
	 * 
	 * @param openable
	 *            开放接口对象
	 * @throws UnregulatedInterfaceException
	 *             不规范的开放接口异常
	 * @see InvalidPathException
	 * @see DuplicatePathException
	 */
	void register(Object openable) throws UnregulatedInterfaceException;

	/**
	 * 撤销开放接口api的注册
	 * 
	 * @param openable
	 *            开放接口对象
	 * @throws IllegalArgumentException
	 *             当参数不是可开放接口的对象
	 */
	void revoke(Object openable);

	/**
	 * 对容器进行特性配置
	 * 
	 * @param feature
	 *            特性
	 * @param on
	 *            打开或关闭
	 * @return true:如果设置成功 false:否则
	 */
	void configure(Feature feature, boolean on);

	/**
	 * 判断容器是否支持指定的特性,容器优先判断用户对容器做过的特性配置,如果没有进行过配置则采用特性的默认模式
	 * 
	 * @param feature
	 *            特性
	 * @return 是否支持,true:如果支持 false:否则
	 */
	boolean supports(Feature feature);

}
