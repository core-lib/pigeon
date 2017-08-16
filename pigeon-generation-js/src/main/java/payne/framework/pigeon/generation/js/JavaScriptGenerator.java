package payne.framework.pigeon.generation.js;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.generation.Generation;
import payne.framework.pigeon.generation.Generator;
import payne.framework.pigeon.generation.Interface;
import payne.framework.pigeon.generation.Model;
import payne.framework.pigeon.generation.exception.GeneratorException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Properties;
import java.util.Set;

/**
 * Created by yangchangpei on 17/8/16.
 */
public class JavaScriptGenerator extends Generator {

    private final File directory;

    public JavaScriptGenerator(String directory) throws IOException {
        this(new File(directory));
    }

    public JavaScriptGenerator(File directory) throws IOException {
        super("pigeon-generation-js.properties");
        this.directory = directory;
    }

    public JavaScriptGenerator(Properties properties, String directory) throws IOException {
        this(properties, new File(directory));
    }

    public JavaScriptGenerator(Properties properties, File directory) throws IOException {
        super(properties);
        this.directory = directory;
    }

    public JavaScriptGenerator(String pathToProperties, String directory) throws IOException {
        this(pathToProperties, new File(directory));
    }

    public JavaScriptGenerator(String pathToProperties, File directory) throws IOException {
        super(pathToProperties);
        this.directory = directory;
    }

    @Override
    public void generate(Interface _interface) throws GeneratorException, IOException {
        if (!Pigeons.isOpenableInterface(_interface.getType())) {
            throw new GeneratorException(_interface.getType() + " is not an openable interface");
        }

        VelocityContext context = new VelocityContext();
        context.put("properties", properties);
        context.put("converter", this);
        context.put("interface", _interface);
        context.put("functions", _interface.getFunctions());

        String prefix = properties.getProperty("prefix");
        String suffix = properties.getProperty("suffix");
        Template template = engine.getTemplate(properties.getProperty("interface"));
        Generation generation = new Generation(template, prefix, suffix);
        generation.generate(context, directory, _interface);
    }

    @Override
    public void generate(Model model) throws GeneratorException, IOException {

    }

    @Override
    public Set<Class<?>> imports(Type type) {
        return null;
    }

    @Override
    public String getName(Type type) {
        return null;
    }

}
