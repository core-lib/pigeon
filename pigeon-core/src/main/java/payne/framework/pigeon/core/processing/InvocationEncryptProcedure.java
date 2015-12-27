package payne.framework.pigeon.core.processing;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Constants;
import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.annotation.Encrypt;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.encoding.InvocationEncoder;
import payne.framework.pigeon.core.encryption.InvocationEncryptor;
import payne.framework.pigeon.core.exception.IllegalConfigurationException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.stream.StreamFactory;
import payne.framework.pigeon.core.key.AsymmetricSecureKey;
import payne.framework.pigeon.core.key.Key;
import payne.framework.pigeon.core.key.SymmetricSecureKey;
import payne.framework.pigeon.core.protocol.Channel;

public class InvocationEncryptProcedure implements Procedure<Encrypt>, Constants {

	public void initialize(int side, Process process, Encrypt annotation, BeanFactory beanFactory, StreamFactory streamFactory, Header header, Channel channel) throws Exception {
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

	public OutputStream wrap(int side, Process process, Encrypt annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, OutputStream outputStream) throws Exception {
		if (side == SIDE_CLIENT) {
			// 生成对称密钥
			InvocationEncryptor encryptor = beanFactory.get(annotation.algorithm(), InvocationEncryptor.class);
			Key secretKey = encryptor.generate(annotation.keysize());
			channel.addAttribute(CHANNEL_SECRET_KEY_ATTRIBUTE_KEY, secretKey);
			// 1.获取公钥
			Key publicKey = (Key) (Key) channel.getAttribute(CHANNEL_PUBLIC_KEY_ATTRIBUTE_KEY);
			encryptor = beanFactory.get(publicKey.getAlgorithm(), InvocationEncryptor.class);
			// 2.对对称密钥加密
			byte[] key = encryptor.encrypt(publicKey, publicKey.getSize(), secretKey.getDecryptKey());
			// 3.用指定编码并写入
			InvocationEncoder encoder = beanFactory.get(annotation.keyEncoding().value(), InvocationEncoder.class);
			outputStream.write(encoder.encode(key));
			// 4.写入分割线
			outputStream.write(annotation.separator());
			// 5.对向量加密
			encoder = beanFactory.get(annotation.ivEncoding().value(), InvocationEncoder.class);
			byte[] iv = encryptor.encrypt(publicKey, publicKey.getSize(), secretKey.getIv());
			// 6.用指定编码并写入
			outputStream.write(encoder.encode(iv));
			// 7.写入分割线
			outputStream.write(annotation.separator());
		}

		InvocationEncryptor encryptor = beanFactory.get(annotation.algorithm(), InvocationEncryptor.class);
		Key key = (Key) channel.getAttribute(CHANNEL_SECRET_KEY_ATTRIBUTE_KEY);
		return encryptor.wrap(key, key.getSize(), outputStream);
	}

	public InputStream wrap(int side, Process process, Encrypt annotation, BeanFactory beanFactory, StreamFactory streamFactory, Channel channel, Header header, InputStream inputStream) throws Exception {
		if (side == SIDE_SERVER) {
			// 获取非对称私钥
			Key privateKey = (Key) channel.getAttribute(CHANNEL_PRIVATE_KEY_ATTRIBUTE_KEY);
			InvocationEncryptor encryptor = beanFactory.get(privateKey.getAlgorithm(), InvocationEncryptor.class);
			// 1.读取对称密钥
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int b = 0;
			while ((b = inputStream.read()) != annotation.separator()) {
				out.write(b);
			}
			// 2.解码密钥
			InvocationEncoder encoder = beanFactory.get(annotation.keyEncoding().value(), InvocationEncoder.class);
			byte[] key = encoder.decode(out.toByteArray());
			// 3.解密密钥
			key = encryptor.decrypt(privateKey, privateKey.getSize(), key);
			// 4.读取向量
			out.reset();
			while ((b = inputStream.read()) != annotation.separator()) {
				out.write(b);
			}
			// 5.解码向量
			encoder = beanFactory.get(annotation.ivEncoding().value(), InvocationEncoder.class);
			byte[] iv = encoder.decode(out.toByteArray());
			// 6.解密向量
			iv = encryptor.decrypt(privateKey, privateKey.getSize(), iv);
			// 7.构建密钥
			Key secretKey = new SymmetricSecureKey(annotation.algorithm(), annotation.keysize(), key, iv);
			channel.addAttribute(CHANNEL_SECRET_KEY_ATTRIBUTE_KEY, secretKey);
		}

		InvocationEncryptor encryptor = beanFactory.get(annotation.algorithm(), InvocationEncryptor.class);
		Key key = (Key) channel.getAttribute(CHANNEL_SECRET_KEY_ATTRIBUTE_KEY);
		return encryptor.wrap(key, key.getSize(), inputStream);
	}

}
