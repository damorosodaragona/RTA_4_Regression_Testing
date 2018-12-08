package testselector.util;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Test;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.Tag;
import testselector.main.Main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


public class Util {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static boolean isJUNIT4TestCase(Method method) {
        Class testClass = method.getDeclaringClass();

        if (method.isAnnotationPresent(Test.class)) {
            return true;
        }
        try {
            Class<?> superClass = testClass.getSuperclass();
            Method inheritedMethod = superClass.getMethod(method.getName(), method.getParameterTypes());
            return isJUNIT4TestCase(inheritedMethod);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static boolean isJUNIT3TestCase(Method method) {
        return method.getName().startsWith("test") && TestCase.class.isAssignableFrom(method.getDeclaringClass());
    }

    /**
     * Try to find a method.
     *
     * @param methodName   a String method's name
     * @param className    a String method's class name
     * @param packageName  a String method's package name
     * @param pathsProject a List of String that contains the paths where find the classes file in which to look.
     * @return the Method found or null if not found.
     */
    public static Method findMethod(String methodName, String className, String packageName, List<String> pathsProject) {
        try {
            String formatClassName = packageName.concat(".").concat(className);
            testselector.util.ClassPathUpdater.add(pathsProject);
            ClassLoader standardClassLoader = Thread.currentThread().getContextClassLoader();
            Class<?> cls = Class.forName(formatClassName, false, standardClassLoader);
            Method m = cls.getDeclaredMethod(methodName);
            m.setAccessible(true);
            return m;

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IOException | InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
            return null;

        }
    }


    private static boolean isJUNIT4TestCase(SootMethod sootMethod) {
        SootClass testClass = sootMethod.getDeclaringClass();

        for (Tag t : sootMethod.getTags()) {
            if (t.toString().contains("junit") && t.toString().contains("Test"))
                return true;

        }
        try {
            SootClass superClass = testClass.getSuperclass();
            if (superClass != null) {
                SootMethod inheritedMethod = superClass.getMethod(sootMethod.getName(), sootMethod.getParameterTypes());
                if (inheritedMethod != null)
                    return isJUNIT4TestCase(inheritedMethod);
            }
        } catch (RuntimeException e) {
            return false;
        }
        return false;
    }

    //TODO: va bene cosi?
    private static boolean isJUNIT3TestCase(SootMethod method) {
        return method.getName().startsWith("test") /*&& junit.framework.TestCase.class.isAssignableFrom(method.getDeclaringClass())*/;
    }


    private static boolean isJUNIT5TestCase(SootMethod sootMethod) {
        SootClass testClass = sootMethod.getDeclaringClass();

        for (Tag t : sootMethod.getTags()) {
            if (t.toString().contains("junit") && t.toString().contains("Test"))
                return true;

        }
        try {
            SootClass superClass = testClass.getSuperclass();
            if (superClass != null) {
                SootMethod inheritedMethod = superClass.getMethod(sootMethod.getName(), sootMethod.getParameterTypes());
                if (inheritedMethod != null)
                    return isJUNIT5TestCase(inheritedMethod);
            }
        } catch (RuntimeException e) {
            return false;
        }
        return false;
    }

    private static boolean isJUNIT5TestCase(Method method) {
        Class testClass = method.getDeclaringClass();
        if (testClass.equals(Object.class)) {
            return false;
        }
        if (method.isAnnotationPresent(org.junit.jupiter.api.Test.class)) {
            return true;
        }
        try {
            Class<?> superClass = testClass.getSuperclass();
            Method inheritedMethod = superClass.getMethod(method.getName(), method.getParameterTypes());
            return isJUNIT5TestCase(inheritedMethod);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * <pre>
     * This method check if the T object passed is a Junit3/4/5 test method.
     * A method is a Junit3 test method if the method's name starts with "test" and if the class of the methods extend JUnit TestClass
     * A method is a Junit4 test method if the method's is noted with JUnit 4 @Test annotation.
     * A method is a Junit5 test method if the method's is noted with JUnit 5 @Test annotation.
     * @param t the method to check. This can be or a Method objcet or a SootMethod object.
     * @param <T>
     * @return true if is a JUnit3/4/5 method false if not.
     * </pre>
     */
    public static <T> boolean isJunitTestCase(T t) {
        if (t.getClass() == Method.class) {
            Method m = (Method) t;
            return isJUNIT3TestCase(m) || isJUNIT4TestCase(m) || isJUNIT5TestCase(m);
        } else if (t.getClass() == SootMethod.class) {
            SootMethod m = (SootMethod) t;
            return isJUNIT3TestCase(m) || isJUNIT4TestCase(m) || isJUNIT5TestCase(m);

        }
        return false;
    }

    /**
     * Check if a SootMethod ia a JUnit 3/4/5 Method, so if is noted with @Before, @BeforeClass, @After, @AfterClass or @Test.
     * @param m the method to check
     * @return true if is a JUnit 3/4/% method, false if not.
     */
    public static boolean isATestMethod(SootMethod m) {
        return isJunitTestCase(m) || isJunit4TestMethod(m) || isJunit3TestMethod(m);
    }

    private static boolean isJunit4TestMethod(SootMethod m) {
        for (Tag t : m.getTags()) {
            if (t.toString().contains("junit"))
                if (t.toString().contains("Before") || t.toString().contains("After") || t.toString().contains("AfterClass") || t.toString().contains("BeforeClass"))

                    return true;

        }
        return false;
    }


    private static boolean isJunit3TestMethod(SootMethod m) {
        return m.getName().equals("setUp") || m.getName().equals("tearDown");


    }
}