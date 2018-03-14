package payne.framework.pigeon.generation;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.javadoc.Main;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 文档工具类
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-03-12 12:25
 **/
public abstract class DocKit {
    private static Map<Class<?>, ClassDoc> cache = new HashMap<Class<?>, ClassDoc>();
    private static List<String> allJavaFiles;
    /**
     * root
     */
    private static RootDoc root;

    public synchronized static boolean start(RootDoc root) {
        DocKit.root = root;
        return true;
    }

    public synchronized static ClassDoc getClassDoc(Class<?> clazz) {
        if (cache.containsKey(clazz)) return cache.get(clazz);

        String path = getProjectClassAbsolutePath(clazz);
        if (path == null) return null;
        String classpath = System.getProperty("java.class.path");
        Main.execute(new String[]{
                "-doclet",
                DocKit.class.getName(),
                "-encoding",
                "utf-8",
                "-classpath",
                classpath,
                path
        });
        ClassDoc doc = root == null ? null : root.classNamed(clazz.getName());

        cache.put(clazz, doc);

        return doc;
    }

    public static void main(String... args) {
        String s = DocKit.forProperty(DocKit.class, "name");
        System.out.println(s);
    }

    private static String getProjectClassAbsolutePath(Class<?> clazz) {
        List<String> files = getProjectAllJavaFiles();
        String path = "/" + clazz.getName().replace('.', '/') + ".java";
        for (String file : files) if (file.endsWith(path)) return file;
        return null;
    }

    private static List<String> getProjectAllJavaFiles() {
        if (allJavaFiles != null) return allJavaFiles;
        return allJavaFiles = getAllJavaFiles(new File(System.getProperty("user.dir")));
    }

    private static List<String> getAllJavaFiles(File root) {
        if (root.isDirectory()) {
            List<String> files = new ArrayList<String>();
            File[] subs = root.listFiles();
            for (int i = 0; subs != null && i < subs.length; i++) files.addAll(getAllJavaFiles(subs[i]));
            return files;
        } else if (root.isFile() && root.getName().endsWith(".java")) {
            return Collections.singletonList(root.getAbsolutePath().replace('\\', '/'));
        } else {
            return Collections.emptyList();
        }
    }

    public static String forClass(Class<?> clazz) {
        ClassDoc doc = getClassDoc(clazz);
        String comment = doc != null ? doc.getRawCommentText() : null;
        return comment != null ? format(comment) : null;
    }

    public static String forProperty(Class<?> clazz, String field) {
        ClassDoc doc = getClassDoc(clazz);
        if (doc == null) return null;
        FieldDoc[] fields = doc.fields(false);
        for (FieldDoc fd : fields) if (fd.name().equals(field)) return fd.getRawCommentText() != null ? format(fd.getRawCommentText()) : null;
        return null;
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

}
