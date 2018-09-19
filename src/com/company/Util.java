package com.company;

import junit.framework.TestCase;
import org.junit.Test;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.Tag;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {
    private static final Logger LOGGER = Logger.getLogger(Util.class.getName());

    public static boolean isJUNIT4TestCase(Method method) {
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

    public static boolean isJUNIT3TestCase(Method method) {
        return method.getName().startsWith("test") && TestCase.class.isAssignableFrom(method.getDeclaringClass());
    }


    public static Method findMethod(String methodName, String className, String packageName, String pathProject) {
        try {
            String formatClassName = packageName.concat(".").concat(className);
            ClassPathUpdater.add(pathProject + "/");
            ClassLoader standardClassLoader = Thread.currentThread().getContextClassLoader();
            Class<?> cls = Class.forName(formatClassName, false, standardClassLoader);
            return cls.getMethod(methodName);

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IOException | InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public static boolean isJUNIT4TestCase(SootMethod sootMethod) {
        SootClass testClass = sootMethod.getDeclaringClass();
        if (testClass.equals(Object.class)) {
            return false;
        }
        Iterator<Tag> csTag = sootMethod.getTags().iterator();
        while (csTag.hasNext()) {
            Tag t = csTag.next();
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
    public static boolean isJUNIT3TestCase(SootMethod method) {
        return method.getName().startsWith("test") /*&& junit.framework.TestCase.class.isAssignableFrom(method.getDeclaringClass())*/;
    }





}
