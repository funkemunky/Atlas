package cc.funkemunky.api.utils.compiler;

public class CompileUtil {

    /*public Object invoke(Object instance, String methodName, boolean isVoid, Object... args) {
        String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        String packageName = CompileUtil.class.getPackage().getName()
                + ".generated." + instance.getClass().getSimpleName() + methodName;
        String simpleClassName = instance.getClass().getSimpleName() + "$" + methodName;
        String fullClassName = packageName + "." + simpleClassName;
        AtomicInteger integer = new AtomicInteger(0);
        String[] argsString = Arrays.stream(args)
                .map(arg -> arg.getClass().getSimpleName() + " " + integer.incrementAndGet()).toArray(String[]::new);
        final String source = "package " + packageName + ";\n"
                + "public class " + simpleClassName + " implements " + MethodInvoker.class.getName() + " {\n"
                + "    public Object invoke(Object instance, " + (args.length > 0 ? String.join(", ", argsString)
                + ") {\n"
                + (!isVoid ?
                "        return \n": "")
                + "((" +instance.getClass().getName() + ")instance)." + methodName + "();\n"
                + (isVoid ? "return null;\n" : "")
                + "    }\n"
                + "}";
        StringGeneratedJavaCompilerFacade compilerFacade = new StringGeneratedJavaCompilerFacade(
                JavaCompilerBeanPropertyReaderFactory.class.getClassLoader());
        Class<? extends BeanPropertyReader> compiledClass = compilerFacade.compile(
                fullClassName, source, BeanPropertyReader.class);
        try {
            return compiledClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("The generated class (" + fullClassName + ") failed to instantiate.", e);
        }
    }

    public interface MethodInvoker {
        Object invoke(Object instance, Object... objects);
    }*/

}
