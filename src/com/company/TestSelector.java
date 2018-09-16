package com.company;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class TestSelector {


    private final HashMap<Method, ArrayList<String>> differentMethodAndTheirTest;
    private final HashMap<Method, ArrayList<String>> othersMethodsNotPresentInOldProjectAndTheirTest;
    private final Project previousProjectVersion;
    private final Project newProjectVersion;
    private boolean isFindChangeCalled;


    public TestSelector(Project previousProjectVersion, Project newProjectVersion) {
        differentMethodAndTheirTest = new HashMap<>();
        othersMethodsNotPresentInOldProjectAndTheirTest = new HashMap<>();
        this.previousProjectVersion = previousProjectVersion;
        this.newProjectVersion = newProjectVersion;
        isFindChangeCalled = false;
    }

    public HashMap<Method, ArrayList<String>> getDifferentMethodAndTheirTest() {
        return differentMethodAndTheirTest;
    }

    public HashMap<Method, ArrayList<String>> getOthersMethodsNotPresentInOldProjectAndTheirTest() {
        return othersMethodsNotPresentInOldProjectAndTheirTest;
    }

    public Set<Method> getTestToRunForChangedMethods() {
        return differentMethodAndTheirTest.keySet();
    }

    public Set<Method> getTestToRunForNewOrRemoveMethods() {
        return othersMethodsNotPresentInOldProjectAndTheirTest.keySet();
    }

    public Collection<ArrayList<String>> getChangedMethods() {
        return differentMethodAndTheirTest.values();
    }

    public Collection<ArrayList<String>> getNewOrRemovedMethods() {
        return othersMethodsNotPresentInOldProjectAndTheirTest.values();
    }

    public Set<Method> getAllTestToRun() {
        Set<Method> allTest = new HashSet<>();
        allTest.addAll(getTestToRunForChangedMethods());
        allTest.addAll(getTestToRunForNewOrRemoveMethods());
        return allTest;
    }

    /*

    //TODO: 1. Ha senso confrontare solo i methodi se sono nella stessa classe sia in previousProjectVersion che in newProjectVersion? Se un metodo nella nuova versione viene spostao in un altra classe? si
    //TODO: 2. Il metodo ".equivHashCode()" è il metodo giusto per sapere se un metodo ha lo stesso signature? E che altro? Se un metodo viene cambiato da "public" a "protected" o viceversa l'hash code cambia? L'hasc-ocde cambia ma è giusto che cambi e che il metodo venga testato se viene cambiata la signature.
    //TODO: 3. Il metodo ".getActiveBody()" è il metodo giusto per confrontare due metodi? sembrerebbe di si
    //TODO 4: ALARM --> " newProjectVersion.getCallGraph().edgesOutOf(method);" (riga 44) non cicla in fondo all'albero ma si limita al primo nodo dopo l'entry point. Se testA chiama b e b chiama c, riusciamo a recuperare solo b e non c.
    /**
     * This methos is usefull to finc that method that are different between 2 projcets.
     * In particular analyze the methods that each test of each project test and try to find the methods that are different,
     * so the methods that i have same change in the new version of the project, in this way user can know how test have to run
     * for the new project.
     *
     * @param previousProjectVersion  the old version of the project
     * @param newProjectVersion the new version of the project
     * @return A map that contains as key the String with the name of the test that have to run and as value the method fot that test that are different.
     */
   /*
    public static HashMap<Method, ArrayList<String>> selectTest(Project previousProjectVersion, Project newProjectVersion) {

        HashMap<Method, ArrayList<String>> find = new HashMap<>();
        Iterator<SootMethod> it = newProjectVersion.getEntryPoints().iterator();
        while (it.hasNext()) {
            SootMethod method = it.next();
            Iterator<Edge> iteratorp1 = newProjectVersion.getCallGraph().edgesOutOf(method);
            while (iteratorp1.hasNext()) {
                Edge edgeP1 = iteratorp1.next();
                System.out.println(edgeP1);
                MethodOrMethodContext src = edgeP1.getSrc();
                MethodOrMethodContext tgt = edgeP1.getTgt();
                Iterator<Edge> iterator = previousProjectVersion.getCallGraph().iterator();


                while (iterator.hasNext()) {
                    SootMethod tgtp = iterator.next().tgt().method();
                    //confronta il nome della classe che si sta analizzando di previousProjectVersion e di newProjectVersion, se il nome è uguale:
                    if (tgtp.getDeclaringClass().getJavaStyleName().equals(tgt.method().getDeclaringClass().getJavaStyleName())) {
                        //controlla che l'hash code del metodo che si sta analizzando di previousProjectVersion sia uguale all'hascode di newProjectVersion (cioè se sono lo stesso metodo (?)), se è uguale:
                        if (tgtp.equivHashCode() == tgt.method().equivHashCode()) {
                            //controlla che il byteode del corpo del metodo sia uguale, se non lo è significa che in newProjectVersion è stato modificato, quindi:
                            if (!tgtp.method().getActiveBody().toString().equals(tgt.method().getActiveBody().toString())) {
                                //prende il metodo di test che testa questa metodo modificato e lo cerca nella classe in modo da averlo sotto-forma di tipo Method
                                Method testMethod = findMethod(src.method().getName(), src.method().getDeclaringClass().getJavaPackageName() + "." + src.method().getDeclaringClass().getJavaStyleName(), newProjectVersion.getPath());
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


    private void callGraphsAnalyzer(Edge e, SootMethod entryPoint, ArrayList<Edge> yetAnalyzed) {

        SootMethod m1 = e.getTgt().method();
        CallGraph pCallGraph = previousProjectVersion.getCallGraph();
        boolean isOldMethod = false;
        for (Edge edge : pCallGraph) {
            SootMethod m = edge.getTgt().method();
            if (isTheSame(m, m1)) {
                isOldMethod = true;
                if (!equals(m, m1)) {
                    Method test = Util.findMethod(entryPoint.getName(), entryPoint.getDeclaringClass().getJavaStyleName(), entryPoint.getDeclaringClass().getJavaPackageName(), newProjectVersion.getPath());
                    assert test != null;
                    if (Util.isJUNIT4TestCase(test) || Util.isJUNIT3TestCase(test)) {
                        if (!differentMethodAndTheirTest.containsKey(test)) {
                            ArrayList<String> methods = new ArrayList<>();
                            differentMethodAndTheirTest.put(test, methods);
                        }
                        differentMethodAndTheirTest.get(test).add(m1.getName());
                        break;
                    }
                }
            }
        }

        if (!isOldMethod) {
            Method test = Util.findMethod(entryPoint.getName(), entryPoint.getDeclaringClass().getJavaStyleName(), entryPoint.getDeclaringClass().getJavaPackageName(), newProjectVersion.getPath());
            assert test != null;
            if (Util.isJUNIT4TestCase(test) || Util.isJUNIT3TestCase(test)) {
                if (!othersMethodsNotPresentInOldProjectAndTheirTest.containsKey(test)) {
                    ArrayList<String> methods = new ArrayList<>();
                    othersMethodsNotPresentInOldProjectAndTheirTest.put(test, methods);
                }
                othersMethodsNotPresentInOldProjectAndTheirTest.get(test).add(m1.getName());

            }
        }

        yetAnalyzed.add(e);
        SootMethod bho = e.getTgt().method();
        Iterator<Edge> bho1 = newProjectVersion.getCallGraph().edgesOutOf(bho);
        Edge e3;

        while (bho1.hasNext()) {
            e3 = bho1.next();
            if (!yetAnalyzed.contains(e3))
                callGraphsAnalyzer(e3, entryPoint, yetAnalyzed);
        }
    }

    public Set<Method> selectTest() {
        isFindChangeCalled = true;
        Iterator<SootMethod> it = newProjectVersion.getEntryPoints().iterator();
        ArrayList<Edge> yetAnalyzed = new ArrayList<>();
        while (it.hasNext()) {
            SootMethod method = it.next();
            Iterator<Edge> iteratorp1 = newProjectVersion.getCallGraph().edgesOutOf(method);
            callGraphsAnalyzer(iteratorp1.next(), method, yetAnalyzed);
        }
        return getAllTestToRun();
    }

    private boolean equals(SootMethod m, SootMethod m1) {


        return m.getActiveBody().toString().equals(m1.getActiveBody().toString());

    }

    private boolean isTheSame(SootMethod m, SootMethod m1) {
        return (m.getDeclaringClass().getJavaStyleName().equals(m1.getDeclaringClass().getJavaStyleName()) && m.equivHashCode() == m1.equivHashCode());
    }


    /**
     * This method try to run the method passed as parameter in the class passe as parameter, in the project pass as parameter.
     * This method is udefull to lunch the tests fo a project. For now if the lunch of the test faill, try to find a setuUp method and lunch that method before and thand the method passed as parameere.
     *
     * @param method    the method to run
     * @param testClass the class that contains the method to run
     */
    private void runTestMethod(Class testClass, Method method) {
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


    public Set<Method> runTestMethods() throws IllegalStateException {
        if (!isFindChangeCalled) {
            throw new IllegalStateException("You need to call before 'selectTest()' method ");
        }

        Set<Method> testsToRun = getAllTestToRun();
        try {
            ClassPathUpdater.add(newProjectVersion.getPath() + "/");
            for (Method testMethod : testsToRun) {
                runTestMethod(testMethod.getDeclaringClass(), testMethod);
            }
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return testsToRun;
    }


}



