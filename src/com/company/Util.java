package com.company;

import junit.framework.TestCase;
import org.junit.Test;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.Tag;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {
    private static final Logger LOGGER = Logger.getLogger(Util.class.getName());

    private static boolean isJUNIT4TestCase(Method method) {
        Class testClass = method.getDeclaringClass();
        if (testClass.equals(Object.class)) {
            return false;
        }
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


    public static Method findMethod(String methodName, String className, String packageName, ArrayList<String> pathsProject) {
        try {
            String formatClassName = packageName.concat(".").concat(className);
            ClassPathUpdater.add(pathsProject);
            ClassLoader standardClassLoader = Thread.currentThread().getContextClassLoader();
            Class<?> cls = Class.forName(formatClassName, false, standardClassLoader);
            Method m = cls.getDeclaredMethod(methodName);
            m.setAccessible(true);
            return m;

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IOException | InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
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
                    return isJUNIT4TestCase(inheritedMethod);
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
            return isJUNIT4TestCase(inheritedMethod);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

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

}

