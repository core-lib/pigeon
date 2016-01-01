package payne.framework.pigeon.core.protocol;

import java.io.OutputStream;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.httpclient.ChunkedOutputStream;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.formatting.FormatInvocationOutputStream;
import payne.framework.pigeon.core.formatting.InvocationFormatter;
import payne.framework.pigeon.core.processing.InvocationFormatProcedure;
import payne.framework.pigeon.core.processing.Step;
import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.core.toolkit.WritableOutputStream;

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
 * @date 2015年10月18日 下午4:04:49
 *
 * @version 1.0.0
 */
public class ChunkedInvocationWriter implements HTTPInvocationWriter {
	private final Channel channel;
	private final Header serverHeader;

	public ChunkedInvocationWriter(Channel channel, Header serverHeader) {
		super();
		this.channel = channel;
		this.serverHeader = serverHeader;
	}

	public void write(Invocation invocation, BeanFactory beanFactory, StreamFactory streamFactory, List<Step> steps) throws Exception {
		OutputStream wrap = null;
		try {
			InvocationFormatter formatter = beanFactory.get(serverHeader.getContentType(), InvocationFormatter.class);
			InvocationFormatProcedure procedure = new InvocationFormatProcedure(formatter, invocation);
			Step step = new Step(null, null, procedure);
			steps.add(0, step);

			wrap = new ChunkedOutputStream(new WritableOutputStream(channel));

			for (int i = steps.size() - 1; i >= 0; i--) {
				Step s = steps.get(i);
				wrap = s.wrap(Channel.SIDE_SERVER, beanFactory, streamFactory, channel, serverHeader, wrap);
			}

			channel.setStatus(new State());
			for (Entry<String, String> entry : serverHeader.entrySet()) {
				IOToolkit.writeLine(entry.getKey() + ": " + entry.getValue(), channel);
			}
			IOToolkit.writeLine("", channel);

			FormatInvocationOutputStream fios = (FormatInvocationOutputStream) wrap;
			fios.serialize(invocation.getResult());
			fios.flush();
		} finally {
			IOToolkit.close(wrap);
		}
	}

}
