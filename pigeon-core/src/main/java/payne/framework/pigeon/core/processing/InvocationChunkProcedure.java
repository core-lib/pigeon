package payne.framework.pigeon.core.processing;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.annotation.Chunk;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.protocol.Channel;
import payne.framework.pigeon.core.protocol.Chunkable;

/**
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author yangchangpei 646742615@qq.com
 *
 * @date 2015年10月19日 上午11:34:59
 *
 * @version 1.0.0
 */
public class InvocationChunkProcedure implements Procedure<Chunk> {

	public void initialize(int side, Process process, Chunk annotation, BeanFactory beanFactory, StreamFactory streamFactory, Header header, Channel channel) throws Exception {
		if (side == Channel.SIDE_CLIENT && channel instanceof Chunkable) {
			Chunkable chunkable = (Chunkable) channel;
			chunkable.setChunksize(annotation.size());
		}
	}

	public OutputStream wrap(int side, Process process, Chunk annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, OutputStream outputStream) throws Exception {
		return outputStream;
	}

	public InputStream wrap(int side, Process process, Chunk annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, InputStream inputStream) throws Exception {
		return inputStream;
	}

}
