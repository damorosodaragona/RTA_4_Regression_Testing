package com.company;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import soot.Kind;
import soot.MethodOrMethodContext;

import soot.SootMethod;
import soot.jimple.toolkits.callgraph.Edge;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


import java.lang.reflect.Method;

public class Util {
/*

    //TODO: 1. Ha senso confrontare solo i methodi se sono nella stessa classe sia in p che in p1? Se un metodo nella nuova versione viene spostao in un altra classe? si
    //TODO: 2. Il metodo ".equivHashCode()" è il metodo giusto per sapere se un metodo ha lo stesso signature? E che altro? Se un metodo viene cambiato da "public" a "protected" o viceversa l'hash code cambia? L'hasc-ocde cambia ma è giusto che cambi e che il metodo venga testato se viene cambiata la signature.
    //TODO: 3. Il metodo ".getActiveBody()" è il metodo giusto per confrontare due metodi? sembrerebbe di si
    //TODO 4: ALARM --> " p1.getCallGraph().edgesOutOf(method);" (riga 44) non cicla in fondo all'albero ma si limita al primo nodo dopo l'entry point. Se testA chiama b e b chiama c, riusciamo a recuperare solo b e non c.
    /**
     * This methos is usefull to finc that method that are different between 2 projcets.
     * In particular analyze the methods that each test of each project test and try to find the methods that are different,
     * so the methods that i have same change in the new version of the project, in this way user can know how test have to run
     * for the new project.
     *
     * @param p  the old version of the project
     * @param p1 the new version of the project
     * @return A map that contains as key the String with the name of the test that have to run and as value the method fot that test that are different.
     */
   /*
    public static HashMap<Method, ArrayList<String>> findChange(Project p, Project p1) {

        HashMap<Method, ArrayList<String>> find = new HashMap<>();
        Iterator<SootMethod> it = p1.getEntryPoints().iterator();
        while (it.hasNext()) {
            SootMethod method = it.next();
            Iterator<Edge> iteratorp1 = p1.getCallGraph().edgesOutOf(method);
            while (iteratorp1.hasNext()) {
                Edge edgeP1 = iteratorp1.next();
                System.out.println(edgeP1);
                MethodOrMethodContext src = edgeP1.getSrc();
                MethodOrMethodContext tgt = edgeP1.getTgt();
                Iterator<Edge> iterator = p.getCallGraph().iterator();


                while (iterator.hasNext()) {
                    SootMethod tgtp = iterator.next().tgt().method();
                    //confronta il nome della classe che si sta analizzando di p e di p1, se il nome è uguale:
                    if (tgtp.getDeclaringClass().getJavaStyleName().equals(tgt.method().getDeclaringClass().getJavaStyleName())) {
                        //controlla che l'hash code del metodo che si sta analizzando di p sia uguale all'hascode di p1 (cioè se sono lo stesso metodo (?)), se è uguale:
                        if (tgtp.equivHashCode() == tgt.method().equivHashCode()) {
                            //controlla che il byteode del corpo del metodo sia uguale, se non lo è significa che in p1 è stato modificato, quindi:
                            if (!tgtp.method().getActiveBody().toString().equals(tgt.method().getActiveBody().toString())) {
                                //prende il metodo di test che testa questa metodo modificato e lo cerca nella classe in modo da averlo sotto-forma di tipo Method
                                Method testMethod = findMethod(src.method().getName(), src.method().getDeclaringClass().getJavaPackageName() + "." + src.method().getDeclaringClass().getJavaStyleName(), p1.getPath());
                                //controlla che sia effettivamente un metodo di test, se lo è:
                                if (isJUNIT4TestCase(testMethod, testMethod.getDeclaringClass()) || isJUNIT3TestCase(testMethod, testMethod.getDeclaringClass())) {
                                    //controlla se nella mappa c'è già una chiave con come valore questo metodo di test
                                    if (!find.containsKey(src.method().getName())) {
                                        //se non c'è l'aggiunge come valore un arrylist vuoto
                                        ArrayList<String> addTgt = new ArrayList<>();
                                        find.put(testMethod, addTgt);
                                    }
                                    //aggiunge nella mappa il nome del metodo differente in corrispondenza del metodo di test che lo testa
                                    find.get(testMethod).add(tgt.method().getName());
                                    break;
                                }
                            }

                        }
                    }

                }
            }
        }
        return find;

    }*/


    //Forse
    private static HashMap<Method, ArrayList<String>> ricorsive(Edge e, Project p, Project p1, HashMap<Method, ArrayList<String>> find, SootMethod entryPoint, ArrayList<Edge> yetAnalyzed) {

        SootMethod m1 = e.getTgt().method();
        Iterator<Edge> iterator = p.getCallGraph().iterator();
        while (iterator.hasNext()) {
            SootMethod m = iterator.next().getTgt().method();
            if (isTheSame(m, m1)) {
                if (!equals(m, m1)) {
                    Method test = findMethod(entryPoint.getName(), entryPoint.getDeclaringClass().getJavaPackageName() + "." + entryPoint.getDeclaringClass().getJavaStyleName(), p1.getPath());
                    if (isJUNIT4TestCase(test, test.getDeclaringClass()) || isJUNIT3TestCase(test, test.getDeclaringClass())) {
                        if (!find.containsKey(entryPoint)) {
                            ArrayList<String> methods = new ArrayList<>();
                            find.put(test, methods);
                        }
                        find.get(test).add(m1.getName());
                        break;
                    }
                }
            }
        }

        yetAnalyzed.add(e);
        SootMethod bho = e.getTgt().method();
        Iterator<Edge> bho1 = p1.getCallGraph().edgesOutOf(bho);
        Edge e3 = null;
        Kind k = null;
        while (bho1.hasNext()) {
            e3 = bho1.next();
            //k = e3.kind();
            if (!yetAnalyzed.contains(e3))
                ricorsive(e3, p, p1, find, entryPoint, yetAnalyzed);
        }

        /*if(k.name().equals("INVALID"))
            return find;

        else
             ricorsive(p1.getCallGraph().edgesOutOf(e.getTgt().method()).next(), p,p1,  find, entryPoint);
*/
        return find;
    }

    public static HashMap<Method, ArrayList<String>> findChange(Project p, Project p1) {
        HashMap<Method, ArrayList<String>> find = new HashMap<>();
        Iterator<SootMethod> it = p1.getEntryPoints().iterator();
        ArrayList<Edge> yetAnalyzed = new ArrayList<>();
        while (it.hasNext()) {
            SootMethod method = it.next();
            Iterator<Edge> iteratorp1 = p1.getCallGraph().edgesOutOf(method);
            ricorsive(iteratorp1.next(), p, p1, find, method, yetAnalyzed);
        }
        return find;
    }

    private static boolean equals(SootMethod m, SootMethod m1) {

        return m.getActiveBody().toString().equals(m1.getActiveBody().toString());

    }

    private static boolean isTheSame(SootMethod m, SootMethod m1) {
        return (m.getDeclaringClass().getJavaStyleName().equals(m1.getDeclaringClass().getJavaStyleName()) && m.equivHashCode() == m1.equivHashCode());
    }


    /**
     * This method try to run the method passed as parameter in the class passe as parameter, in the project pass as parameter.
     * This method is udefull to lunch the tests fo a project. For now if the lunch of the test faill, try to find a setuUp method and lunch that method before and thand the method passed as parameere.
     *
     * @param method    the method to run
     * @param testClass the class that contains the method to run
     */
    private static void runTestMethods(Class testClass, Method method) {
        JUnitCore runner = new JUnitCore();
        Request request = Request.method(testClass, method.getName());
        Result result = runner.run(request);
        if (result.wasSuccessful())
            System.out.println("The followinw test case is passsed: " + method.getName());
        else {

            List<Failure> failures = result.getFailures();
            for (Failure failure : failures) {

                failure.getException().printStackTrace();
                System.err.println("The followinw test case is failed:" + method.getName());
                System.err.println(failure);
                System.err.println(failure.getTrace());

            }
        }


    }


    public static Set<Method> runTestMethods(String path, Set<Method> testsToRun) {
        try {
            ClassPathUpdater.add(path + "/");
            Iterator<Method> methodsIterator = testsToRun.iterator();
            while (methodsIterator.hasNext()) {
                Method testMethod = methodsIterator.next();
                runTestMethods(testMethod.getDeclaringClass(), testMethod);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return testsToRun;
    }

    private static boolean isJUNIT4TestCase(Method method, Class<?> testClass) {
        if (testClass.equals(Object.class)) {
            return false;
        }
        if (method.isAnnotationPresent(Test.class)) {
            return true;
        }
        try {
            Class<?> superClass = testClass.getSuperclass();
            Method inheritedMethod = superClass.getMethod(method.getName(), method.getParameterTypes());
            return isJUNIT4TestCase(inheritedMethod, superClass);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static boolean isJUNIT3TestCase(Method method, Class<?> clazz) {
        if (method.getName().startsWith("test") && junit.framework.TestCase.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }


    private static Method findMethod(String methodName, String className, String pathProject) {
        try {

            ClassPathUpdater.add(pathProject + "/");
            ClassLoader standardClassLoader = Thread.currentThread().getContextClassLoader();
            Class<?> cls = Class.forName(className, false, standardClassLoader);
            Method m = cls.getMethod(methodName);
            return m;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}



