package com.company;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Util {
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
        return method.getName().startsWith("test") && junit.framework.TestCase.class.isAssignableFrom(method.getDeclaringClass());
    }

    //TODO: ELIMINARE P DAL CLASS PATH
    public static Method findMethod(String methodName, String className, String packageName, String pathProject) {
        try {
            String formatClassName = packageName.concat(".").concat(className);
            ClassPathUpdater.add(pathProject + "/");
            ClassLoader standardClassLoader = Thread.currentThread().getContextClassLoader();
            Class<?> cls = Class.forName(formatClassName, false, standardClassLoader);
            return cls.getMethod(methodName);

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IOException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
