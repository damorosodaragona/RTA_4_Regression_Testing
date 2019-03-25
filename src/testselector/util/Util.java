package testSelector.util;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.Tag;
import testSelector.main.Main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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
     * @param target a List of String that contains the paths where find the classes file in which to look.
     * @param classPath
     * @return the Method found or null if not found.
     */
    public static Method findMethod(String methodName, String className, String packageName, List<String> target, List<String> classPath) {
        Class<?> cls = null;
        try {

            String formatClassName = packageName.concat(".").concat(className);
            ClassPathUpdater.add(target);
            ClassLoader standardClassLoader = Thread.currentThread().getContextClassLoader();
            cls = Class.forName(formatClassName, false, standardClassLoader);
            Method m = cls.getMethod(methodName);
            m.setAccessible(true);
            return m;

        } catch (  NoClassDefFoundError e) {
            LOGGER.info("try to retrieve: " + packageName.concat(".").concat(className).concat(".").concat(methodName));
            LOGGER.info("try to resolve: "  + e.getMessage());
            for(String jar : classPath){
                reLoad(jar);
            }

            try {
                String formatClassName = packageName.concat(".").concat(className);
                ClassPathUpdater.add(target);
                ClassLoader standardClassLoader = Thread.currentThread().getContextClassLoader();
                cls = Class.forName(formatClassName, false, standardClassLoader);
                Method m = cls.getMethod(methodName);
                m.setAccessible(true);
                m.equals(m);
                return m;
            } catch ( NoClassDefFoundError | IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e1) {
                LOGGER.info("try to retrieve: " + packageName.concat(".").concat(className).concat(".").concat(methodName));
                LOGGER.info("can't resolve: "  + e.getMessage());
            }
        } catch (  ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IOException | InvocationTargetException e) {
            //  LOGGER.error(e.getMessage(), e);

        }
        return null;

    }

    private static Class getClazz(@NotNull SootMethod m){
        String formatClassName = m.getDeclaringClass().getName();
        ClassLoader standardClassLoader = Thread.currentThread().getContextClassLoader();
        Class cls = null;
        try {
            cls = Class.forName(formatClassName, false, standardClassLoader);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            e.printStackTrace();
        }
return cls;
    }

    private static void reLoad(String jarUrl)  {
        File file = new File(jarUrl);
        URL url = null;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
        }
        URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        Method method;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            if(method != null) {
                method.setAccessible(true);
                method.invoke(classLoader, url);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
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
        Class cls = getClazz(method);
        return method.getName().startsWith("test") && junit.framework.TestCase.class.isAssignableFrom(cls);
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
     * @param <T>
     * @param t the method to check. This can be or a Method objcet or a SootMethod object.
     * @param junitVersion
     * @return true if is a JUnit3/4/5 method false if not.
     * </pre>
     */
    public static <T> boolean isJunitTestCase(T t, int junitVersion) {
        if (t.getClass() == Method.class) {
            Method m = (Method) t;
            if(junitVersion == 3)
                return  isJUNIT3TestCase(m);
            else if(junitVersion == 4)
                return isJUNIT4TestCase(m);
            else if(junitVersion == 5)
                return isJUNIT5TestCase(m);
            else
                return isJUNIT3TestCase(m) || isJUNIT4TestCase(m) || isJUNIT5TestCase(m);
        } else if (t.getClass() == SootMethod.class) {
            SootMethod m = (SootMethod) t;
            if(junitVersion == 3)
                return  isJUNIT3TestCase(m);
            else if(junitVersion == 4)
                return isJUNIT4TestCase(m);
            else if(junitVersion == 5)
                return isJUNIT5TestCase(m);
            else
                return isJUNIT3TestCase(m) || isJUNIT4TestCase(m) || isJUNIT5TestCase(m);

        }
        return false;
    }

    /**
     * Check if a SootMethod ia a JUnit 3/4/5 Method, so if is noted with @Before, @BeforeClass, @After, @AfterClass or @Test.
     * @param m the method to check
     * @param junitVersion
     * @return true if is a JUnit 3/4/% method, false if not.
     */
    public static boolean isATestMethod(SootMethod m, int junitVersion) {
        if(!isJunitTestCase(m,junitVersion )){
            if(junitVersion == 3){
                return isJunit3TestMethod(m);
            } else if (junitVersion == 4){
                return isJunit4TestMethod(m);
            }//TODO: AGGIUMGERE PER JUNIT5
        }
            return true;
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

    public static boolean isSetup(SootMethod testMethod, int junitVersion) {
        if(junitVersion == 4 ){
            for (Tag t : testMethod.getTags()) {
                if (t.toString().contains("junit"))
                    if (t.toString().contains("Before") || t.toString().contains("BeforeClass"))
                        return true;

            }
            return false;
        }
        if(junitVersion == 3){
            return testMethod.getName().equals("setUp");
        }
        if(junitVersion == 5){
            //TODO: da implementare
            return false;
        }

       return false;
    }

    public static boolean isTearDown(SootMethod testMethod, int junitVersion) {
        if(junitVersion == 4 ){
            for (Tag t : testMethod.getTags()) {
                if (t.toString().contains("junit"))
                    if (t.toString().contains("After") || t.toString().contains("AfterClass"))
                        return true;

            }
            return false;
        }
        if(junitVersion == 3){
            return testMethod.getName().equals("tearDown");
        }
        if(junitVersion == 5){
            //TODO: da implementare
            return false;
        }

        return false;
    }
}