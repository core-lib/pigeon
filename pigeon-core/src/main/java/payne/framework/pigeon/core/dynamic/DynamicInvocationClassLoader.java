package payne.framework.pigeon.core.dynamic;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public final class DynamicInvocationClassLoader extends URLClassLoader {
	private static DynamicInvocationClassLoader instance;

	private DynamicInvocationClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public static DynamicInvocationClassLoader getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (DynamicInvocationClassLoader.class) {
			if (instance == null) {
				instance = new DynamicInvocationClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());
			}
		}
		return instance;
	}

	public void addURL(URL url) {
		super.addURL(url);
	}

	public Class<? extends DynamicInvocation> load(String name) throws ClassNotFoundException {
		return loadClass(name).asSubclass(DynamicInvocation.class);
	}

	public Class<? extends DynamicInvocation> load(String name, boolean resolve) throws ClassNotFoundException {
		return loadClass(name, resolve).asSubclass(DynamicInvocation.class);
	}

	public Class<? extends DynamicInvocation> define(String name, byte[] buf) throws ClassFormatError {
		return defineClass(name, buf, 0, buf.length).asSubclass(DynamicInvocation.class);
	}

	public Class<? extends DynamicInvocation> define(String name, byte[] buf, int off, int len) throws ClassFormatError {
		return defineClass(name, buf, off, len).asSubclass(DynamicInvocation.class);
	}

	public Class<? extends DynamicInvocation> define(String name, byte[] buf, int off, int len, CodeSource source) throws ClassFormatError {
		return defineClass(name, buf, off, len, source).asSubclass(DynamicInvocation.class);
	}

	public Class<? extends DynamicInvocation> define(String name, byte[] buf, int off, int len, ProtectionDomain domain) throws ClassFormatError {
		return defineClass(name, buf, off, len, domain).asSubclass(DynamicInvocation.class);
	}

	public Class<? extends DynamicInvocation> define(String name, ByteBuffer buffer, CodeSource source) throws ClassFormatError {
		return defineClass(name, buffer, source).asSubclass(DynamicInvocation.class);
	}

	public Class<? extends DynamicInvocation> define(String name, ByteBuffer buffer, ProtectionDomain domain) throws ClassFormatError {
		return defineClass(name, buffer, domain).asSubclass(DynamicInvocation.class);
	}

}
