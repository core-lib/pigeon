package payne.framework.pigeon.core.processing;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Constants;
import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.annotation.Sign;
import payne.framework.pigeon.core.encoding.InvocationEncoder;
import payne.framework.pigeon.core.exception.IllegalConfigurationException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.key.AsymmetricSecureKey;
import payne.framework.pigeon.core.key.Key;
import payne.framework.pigeon.core.protocol.Channel;
import payne.framework.pigeon.core.signature.InvocationSigner;

public class InvocationSignProcedure implements Procedure<Sign>, Constants {

	public void initialize(int side, Process process, Sign annotation, BeanFactory beanFactory, StreamFactory streamFactory, Header header, Channel channel) throws Exception {
		switch (side) {
		case SIDE_CLIENT: {
			// 获取服务器公钥
			String algorithm = beanFactory.value("public-key-algorithm");
			String encoding = beanFactory.value("public-key-encoding");
			String keysize = beanFactory.value("public-key-size");
			String encoded = beanFactory.value("public-key-encoded");
			// 检查配置信息是否有错
			if (algorithm == null || algorithm.trim().equals("") || !beanFactory.contains(algorithm)) {
				throw new IllegalConfigurationException("can not find or resolve public-key-algorithm configuration");
			}
			if (encoding == null || encoding.trim().equals("") || !beanFactory.contains(encoding)) {
				throw new IllegalConfigurationException("can not find or resolve public-key-encoding configuration");
			}
			if (keysize == null || keysize.trim().equals("") || !keysize.trim().matches("\\d+")) {
				throw new IllegalConfigurationException("can not find or resolve public-key-keysize configuration");
			}
			if (encoded == null || encoded.trim().equals("")) {
				throw new IllegalConfigurationException("can not find or resolve public-key-encoded configuration");
			}

			// 对公钥进行解码
			InvocationEncoder encoder = beanFactory.get(encoding, InvocationEncoder.class);
			byte[] bytes = encoder.decode(encoded.getBytes());
			int size = Integer.valueOf(keysize);
			Key publicKey = new AsymmetricSecureKey(algorithm, size, bytes, null);
			channel.addAttribute(CHANNEL_PUBLIC_KEY_ATTRIBUTE_KEY, publicKey);
		}
			break;
		case SIDE_SERVER: {
			// 获取服务器私钥
			String algorithm = beanFactory.value("private-key-algorithm");
			String encoding = beanFactory.value("private-key-encoding");
			String keysize = beanFactory.value("private-key-size");
			String encoded = beanFactory.value("private-key-encoded");
			// 检查配置信息是否有错
			if (algorithm == null || algorithm.trim().equals("") || !beanFactory.contains(algorithm)) {
				throw new IllegalConfigurationException("can not find or resolve private-key-algorithm configuration");
			}
			if (encoding == null || encoding.trim().equals("") || !beanFactory.contains(encoding)) {
				throw new IllegalConfigurationException("can not find or resolve private-key-encoding configuration");
			}
			if (keysize == null || keysize.trim().equals("") || !keysize.trim().matches("\\d+")) {
				throw new IllegalConfigurationException("can not find or resolve private-key-keysize configuration");
			}
			if (encoded == null || encoded.trim().equals("")) {
				throw new IllegalConfigurationException("can not find or resolve private-key-encoded configuration");
			}

			// 对服务器私钥进行解码
			InvocationEncoder encoder = beanFactory.get(encoding, InvocationEncoder.class);
			byte[] bytes = encoder.decode(encoded.getBytes());
			int size = Integer.valueOf(keysize);
			Key privateKey = new AsymmetricSecureKey(algorithm, size, null, bytes);
			channel.addAttribute(CHANNEL_PRIVATE_KEY_ATTRIBUTE_KEY, privateKey);
		}
			break;
		default:
			throw new IllegalStateException("unknow side");
		}
	}

	public OutputStream wrap(int side, Process process, Sign annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, OutputStream outputStream) throws Exception {
		if (side == SIDE_SERVER) {
			InvocationSigner signer = beanFactory.get(annotation.value(), InvocationSigner.class);
			Key privateKey = (Key) channel.getAttribute(CHANNEL_PRIVATE_KEY_ATTRIBUTE_KEY);
			InvocationEncoder dataEncoder = beanFactory.get(annotation.dataEncoding().value(), InvocationEncoder.class);
			InvocationEncoder signatureEncoder = beanFactory.get(annotation.signatureEncoding().value(), InvocationEncoder.class);
			return signer.wrap(privateKey, outputStream, dataEncoder, signatureEncoder, annotation.separator());
		}
		return outputStream;
	}

	public InputStream wrap(int side, Process process, Sign annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, InputStream inputStream) throws Exception {
		if (side == SIDE_CLIENT) {
			InvocationSigner signer = beanFactory.get(annotation.value(), InvocationSigner.class);
			Key publicKey = (Key) channel.getAttribute(CHANNEL_PUBLIC_KEY_ATTRIBUTE_KEY);
			InvocationEncoder dataEncoder = beanFactory.get(annotation.dataEncoding().value(), InvocationEncoder.class);
			InvocationEncoder signatureEncoder = beanFactory.get(annotation.signatureEncoding().value(), InvocationEncoder.class);
			return signer.wrap(publicKey, inputStream, dataEncoder, signatureEncoder, annotation.separator());
		}
		return inputStream;
	}
}
