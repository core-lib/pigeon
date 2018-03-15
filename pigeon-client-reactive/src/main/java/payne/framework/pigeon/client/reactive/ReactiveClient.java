package payne.framework.pigeon.client.reactive;

import payne.framework.pigeon.client.Client;
import payne.framework.pigeon.core.exception.UnsupportedChannelException;
import payne.framework.pigeon.core.exception.UnsupportedFormatException;
import payne.framework.pigeon.core.factory.bean.SingletonBeanFactory;

import java.util.Properties;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 *
 * @author yangchangpei 646742615@qq.com
 * @version 1.0.0
 * @date 2015年8月5日 上午10:12:10
 */
public class ReactiveClient extends Client {

    public ReactiveClient(String host) {
        this(host, 80);
    }

    public ReactiveClient(String host, int port) {
        super(host, port);
        Properties properties = new Properties();
        properties.put("http", "payne.framework.pigeon.core.protocol.HTTPChannel");
        properties.put("http/1.0", "payne.framework.pigeon.core.protocol.HTTPChannel");
        properties.put("http/1.1", "payne.framework.pigeon.core.protocol.HTTPChannel");
        properties.put("https", "payne.framework.pigeon.core.protocol.HTTPSChannel");
        properties.put("HTTP", "payne.framework.pigeon.core.protocol.HTTPChannel");
        properties.put("HTTP/1.0", "payne.framework.pigeon.core.protocol.HTTPChannel");
        properties.put("HTTP/1.1", "payne.framework.pigeon.core.protocol.HTTPChannel");
        properties.put("HTTPS", "payne.framework.pigeon.core.protocol.HTTPSChannel");
        properties.put("application/x-www-form-urlencoded", "payne.framework.pigeon.core.formatting.FormInvocationFormatter");
        properties.put("application/x-java-serialized-object", "payne.framework.pigeon.core.formatting.ObjectInvocationFormatter");
        properties.put("application/json", "payne.framework.pigeon.core.formatting.jackson.JSONInvocationFormatter");
        properties.put("application/yaml", "payne.framework.pigeon.core.formatting.jackson.YAMLInvocationFormatter");
        properties.put("application/smile", "payne.framework.pigeon.core.formatting.jackson.SmileInvocationFormatter");
        properties.put("application/cbor", "payne.framework.pigeon.core.formatting.jackson.CBORInvocationFormatter");
        beanFactory = new SingletonBeanFactory(properties);
    }

    @Override
    public <T> ReactiveConnection<T> build(String implementation, Class<T> interfase) throws Exception {
        if (!beanFactory.contains(protocol)) {
            throw new UnsupportedChannelException(protocol);
        }
        if (!beanFactory.contains(format)) {
            throw new UnsupportedFormatException(format);
        }
        return new ReactiveConnection<T>(this, implementation, interfase);
    }

}
