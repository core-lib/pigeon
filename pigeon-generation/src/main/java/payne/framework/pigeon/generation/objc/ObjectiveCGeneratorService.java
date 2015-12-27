package payne.framework.pigeon.generation.objc;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import payne.framework.pigeon.core.Document;
import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.annotation.Open;
import payne.framework.pigeon.core.toolkit.Collections;
import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.core.toolkit.ZipToolkit;
import payne.framework.pigeon.generation.GeneratorService;
import payne.framework.pigeon.generation.Interface;
import payne.framework.pigeon.server.InvocationContext;
import payne.framework.pigeon.server.InvocationContextAware;
import payne.framework.pigeon.server.InvocationProcessorRegistry;
import payne.framework.pigeon.server.InvocationProcessorRegistry.Registration;

@Open("/objc")
public class ObjectiveCGeneratorService implements GeneratorService, InvocationContextAware {
	private String directory = System.getProperty("java.io.tmpdir") + "/generation/objc/";
	private boolean cached = false;
	private InvocationContext invocationContext;

	public ObjectiveCGeneratorService() {
		super();
	}

	public ObjectiveCGeneratorService(String directory) {
		super();
		this.directory = directory;
	}

	public ObjectiveCGeneratorService(boolean cached) {
		super();
		this.cached = cached;
	}

	public ObjectiveCGeneratorService(String directory, boolean cached) {
		super();
		this.directory = directory;
		this.cached = cached;
	}

	public Document generate(String implementation, String interfase) throws Exception {
		if (implementation == null || implementation.trim().equals("")) {
			throw new IllegalArgumentException("argument implementation must not be null or empty string");
		}

		String x = Pigeons.getOpenPath(implementation);
		String y = interfase == null || interfase.trim().equals("") ? "/" : Pigeons.getOpenPath(interfase);

		String name = Collections.concatenate((x + y).split("/+"), "-", "").intern();

		synchronized (name) {
			File target = new File(directory, name + ".zip");

			if (cached && target.exists()) {
				return new Document(target.getParent(), target.getName());
			}

			InvocationProcessorRegistry registry = invocationContext.getInvocationProcessorRegistry();

			Set<Registration> registrations = registry.matches("^" + x + y + ".*");

			if (registrations == null || registrations.isEmpty()) {
				throw new IllegalArgumentException("no open interface(s) mapped to path : " + x + y);
			}

			Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
			for (Registration registration : registrations) {
				interfaces.add(registration.getProcessor().getInterfase());
			}

			File source = new File(directory, name);
			if (source.exists()) {
				IOToolkit.delete(source);
			}
			source.mkdirs();

			ObjectiveCGenerator generator = new ObjectiveCGenerator(source);
			for (Class<?> i : interfaces) {
				generator.generate(new Interface(x, i));
			}

			ZipToolkit.pack(source, target);

			return new Document(target.getParent(), target.getName());
		}
	}

	public boolean isCached() {
		return cached;
	}

	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public InvocationContext getInvocationContext() {
		return invocationContext;
	}

	public void setInvocationContext(InvocationContext invocationContext) throws Exception {
		this.invocationContext = invocationContext;
	}

}
