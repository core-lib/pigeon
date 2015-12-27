package payne.framework.pigeon.generation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import payne.framework.pigeon.core.toolkit.IOToolkit;

public class Generation {
	private final Template template;
	private final String prefix;
	private final String suffix;

	public Generation(Template template, String prefix, String suffix) {
		super();
		this.template = template;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public void generate(VelocityContext context, File directory, Generable generable) throws IOException {
		FileOutputStream out = null;
		OutputStreamWriter writer = null;
		try {
			File file = new File(directory, prefix + generable.getName() + suffix);
			out = new FileOutputStream(file);
			writer = new OutputStreamWriter(out);
			template.merge(context, writer);
			writer.flush();
		} finally {
			IOToolkit.close(writer);
			IOToolkit.close(out);
		}
	}

	public boolean exists(File directory, Generable generable) {
		return new File(directory, prefix + generable.getName() + suffix).exists();
	}

	public Template getTemplate() {
		return template;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

}