package payne.framework.pigeon.generation;

import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.annotation.Param;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.exception.BeanInitializeException;
import payne.framework.pigeon.core.processing.Step;
import payne.framework.pigeon.generation.annotation.Name;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class Function extends Annotated {
    private final String implementation;
    private final Class<?> interfase;
    private final String comment;
    private final Method method;
    private final String path;
    private final Set<Parameter> parameters;
    private final Map<Annotation, Process> resolvers;
    private final Result result;

    public Function(String implementation, Class<?> interfase, Method method) throws BeanInitializeException {
        super(method.isAnnotationPresent(Name.class) ? method.getAnnotation(Name.class).value().trim() : method.getName(), method.getAnnotations());

        this.result = method.getReturnType() == Void.TYPE ? null : new Result(method.getGenericReturnType());
        this.implementation = implementation;
        this.interfase = interfase;
        this.comment = Documentations.forMethod(method);
        this.method = method;
        {
            String x = Pigeons.getOpenPath(implementation);
            String y = Pigeons.getOpenPath(interfase);
            String z = Pigeons.getOpenPath(method);
            this.path = Pigeons.getOpenPath(x + y + z);
        }
        {
            Type[] types = method.getGenericParameterTypes();
            Annotation[][] annotations = method.getParameterAnnotations();
            this.parameters = new LinkedHashSet<Parameter>();
            for (int i = 0; i < types.length; i++) {
                Map<Class<? extends Annotation>, Annotation> map = new HashMap<Class<? extends Annotation>, Annotation>();
                for (int j = 0; j < annotations[i].length; j++) {
                    map.put(annotations[i][j].annotationType(), annotations[i][j]);
                }
                String name = map.containsKey(Name.class) ? ((Name) map.get(Name.class)).value() : null;
                name = name != null ? name : map.containsKey(Param.class) ? ((Param) map.get(Param.class)).value() : "arg" + i;
                Parameter parameter = new Parameter(name, annotations[i], types[i]);
                this.parameters.add(parameter);
            }
        }
        {
            this.resolvers = new LinkedHashMap<Annotation, Process>();
            Map<Class<? extends Annotation>, Step> map = Pigeons.getMethodProcessings(method);
            for (Class<? extends Annotation> type : map.keySet()) {
                if (!type.isAnnotationPresent(Process.class)) {
                    continue;
                }
                Process resolve = type.getAnnotation(Process.class);
                this.resolvers.put(map.get(type).getAnnotation(), resolve);
            }
        }
    }

    public String getImplementation() {
        return implementation;
    }

    public Class<?> getInterfase() {
        return interfase;
    }

    @Override
    public String getComment() {
        return comment;
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Set<Parameter> getParameters() {
        return parameters;
    }

    public Map<Annotation, Process> getResolvers() {
        return resolvers;
    }

    public Result getResult() {
        return result;
    }

}
