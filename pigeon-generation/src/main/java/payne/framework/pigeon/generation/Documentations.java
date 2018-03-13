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

    public synchronized static void main(String... args) {
        Documentations.setModule("pigeon-generation");
        String documentation = Documentations.forClass(Documentations.class);
        System.out.println(documentation);
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
        return root.classNamed(clazz.getName());
    }

    /**
     * 获取类型上的注释
     *
     * @param clazz 类型
     * @return 注释内容
     */
    public synchronized static String forClass(Class<?> clazz) {
        return getClassDoc(clazz).getRawCommentText();
    }

    public static String forMethod(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        ClassDoc doc = getClassDoc(clazz);

        for (MethodDoc md : doc.methods()) {
            System.out.println(md.signature());
        }
        return null;
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

    public void test(String str,
            int i) throws Exception {

    }
}
