package payne.framework.pigeon.generation;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.javadoc.Main;

import java.lang.reflect.Method;

/**
 * 文档工具类
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-03-12 12:25
 **/
public abstract class Documentations {
    private static String module = "";
    private static String source = "src/main/java";
    private static RootDoc root;

    public synchronized static boolean start(RootDoc root) {
        Documentations.root = root;
        return true;
    }

    private synchronized static ClassDoc getClassDoc(Class<?> clazz) {
        String classpath = System.getProperty("java.class.path");
        String sourcedir = System.getProperty("user.dir") + "/" + module + "/" + source;
        Main.execute(new String[]{
                "-doclet",
                Documentations.class.getName(),
                "-encoding",
                "utf-8",
                "-classpath",
                classpath,
                sourcedir + "/" + clazz.getName().replace('.', '/') + ".java"
        });
        return root == null ? null : root.classNamed(clazz.getName());
    }

    public static void main(String... args) {
        Documentations.setModule("pigeon-generation");
        String s = Documentations.forClass(Documentations.class);
        System.out.println(s);
    }

    /**
     * 获取类型上的注释
     *
     * @param clazz 类型
     * @return 注释内容
     */
    public synchronized static String forClass(Class<?> clazz) {
        ClassDoc doc = getClassDoc(clazz);
        String comment = doc != null ? doc.getRawCommentText() : null;
        return comment != null ? format(comment) : null;
    }

    public static String forMethod(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        ClassDoc doc = getClassDoc(clazz);
        if (doc == null) return null;
        StringBuilder builder = new StringBuilder();
        builder.append(method.getDeclaringClass().getName())
                .append(".")
                .append(method.getName())
                .append("(");
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            if (i > 0) builder.append(", ");
            Class<?> cl = method.getParameterTypes()[i];
            if (cl.isArray()) {
                int dimensions = 0;
                while (cl.isArray()) {
                    dimensions++;
                    cl = cl.getComponentType();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(cl.getName());
                for (int d = 0; d < dimensions; d++) {
                    sb.append("[]");
                }
                builder.append(sb);
            } else {
                builder.append(cl.getName());
            }
        }
        builder.append(")");
        String signature = builder.toString();
        for (MethodDoc md : doc.methods()) {
            if (signature.equals(md.toString())) {
                String comment = md.getRawCommentText();
                return comment != null ? format(comment) : null;
            }
        }
        return null;
    }

    private static String format(String comment) {
        String[] comments = comment.split("[\r\n]");
        StringBuilder sb = new StringBuilder();
        sb.append("/**");
        for (String line : comments) {
            sb.append("\r\n").append(" *").append(line);
        }
        sb.append("\r\n").append(" */");
        return sb.toString();
    }

    public static String getModule() {
        return module;
    }

    public static void setModule(String module) {
        Documentations.module = module;
    }

    public static String getSource() {
        return source;
    }

    public static void setSource(String source) {
        Documentations.source = source;
    }
}
