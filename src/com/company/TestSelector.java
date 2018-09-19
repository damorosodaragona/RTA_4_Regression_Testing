package com.company;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

public class TestSelector {


    private final HashMap<Method, ArrayList<String>> differentMethodAndTheirTest;
    private final HashMap<Method, ArrayList<String>> equalsMethodAndTheirTest;
    private final HashMap<Method, ArrayList<String>> othersMethodsNotPresentInOldProjectAndTheirTest;
    private final Project previousProjectVersion;
    private final Project newProjectVersion;
    private boolean isFindChangeCalled;
    private static final Logger LOGGER = Logger.getLogger(TestSelector.class.getName());

    public TestSelector(Project previousProjectVersion, Project newProjectVersion) {
        differentMethodAndTheirTest = new HashMap<>();
        equalsMethodAndTheirTest = new HashMap<>();
        othersMethodsNotPresentInOldProjectAndTheirTest = new HashMap<>();
        this.previousProjectVersion = previousProjectVersion;
        this.newProjectVersion = newProjectVersion;
        isFindChangeCalled = false;
    }

    public Map<Method, ArrayList<String>> getDifferentMethodAndTheirTest() {
        return differentMethodAndTheirTest;
    }

    public Map<Method, ArrayList<String>> getOthersMethodsNotPresentInOldProjectAndTheirTest() {
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

    private void callGraphsAnalyzer(Edge e, SootMethod entryPoint, ArrayList<Edge> yetAnalyzed) {

        SootMethod m1 = e.getTgt().method();
        CallGraph pCallGraph = previousProjectVersion.getCallGraph();
        for (Edge edge : pCallGraph) {
            SootMethod m = edge.getTgt().method();
            if (!m.isPhantom()) {
                if (isTheSame(m, m1)) { //sono uguali: si --> cioè sno nello stesso package e hanno lo stesso nome? si
                    Method test = Util.findMethod(entryPoint.getName(), entryPoint.getDeclaringClass().getJavaStyleName(), entryPoint.getDeclaringClass().getJavaPackageName(), newProjectVersion.getPath());
                    assert test != null;
                    if (isTestCase(test)) {
                        if (haveSameHashCode(m, m1)) { //hanno lo stesso hashcode: si
                            if (!isDifferent(m, m1)) {
                                addInMap(m1, test, differentMethodAndTheirTest);
                            } else {
                                addInMap(m1, test, equalsMethodAndTheirTest);
                            }
                        } else {
                            addInMap(m1, test, othersMethodsNotPresentInOldProjectAndTheirTest);
                        }
                        break;

                    }

                }
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

    private void addInMap(SootMethod m1, Method test, HashMap<Method, ArrayList<String>> hashMap) {

        hashMap.putIfAbsent(test, new ArrayList<>());
        hashMap.get(test).add(m1.getName());
    }

    private boolean isTestCase(Method test) {
        return Util.isJUNIT4TestCase(test) || Util.isJUNIT3TestCase(test);
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
        findNewMethods();
        return getAllTestToRun();
    }

    private boolean isDifferent(SootMethod m, SootMethod m1) {


        return m.getActiveBody().toString().equals(m1.getActiveBody().toString());

    }

    private boolean isTheSame(SootMethod m, SootMethod m1) {
        return m.getDeclaringClass().getJavaStyleName().equals(m1.getDeclaringClass().getJavaStyleName()) && m.getName().equals(m1.getName());
    }

    private boolean haveSameHashCode(SootMethod m, SootMethod m1) {
        return m.equivHashCode() == m1.equivHashCode();
    }


    /**
     * This method try to run the method passed as parameter in the class passe as parameter, in the project pass as parameter.
     * This method is udefull to lunch the tests fo a project. For now if the lunch of the test faill, try to find a setuUp method and lunch that method before and thand the method passed as parameere.
     *
     * @param method    the method to run
     * @param testClass the class that contains the method to run
     */

    private void runJUNIT4TestMethod(Class testClass, Method method) {

        JUnitCore runner = new JUnitCore();
        Request request = Request.method(testClass, method.getName());
        Result result = runner.run(request);
        if (result.wasSuccessful())
            LOGGER.info("The followinw test case is passsed: " + method.getName());

        else {

            List<Failure> failures = result.getFailures();
            for (Failure failure : failures) {

                failure.getException().printStackTrace();
                LOGGER.warning("The followinw test case is failed:" + method.getName());
                LOGGER.warning(failure.toString());
                LOGGER.warning(failure.getTrace());

            }
        }


    }


    private void runTestMethod(Class testClass, Method method) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectMethod(testClass, method)
                )
                .build();

        Launcher launcher = LauncherFactory.create();

        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        TestExecutionSummary summary = listener.getSummary();

        List<TestExecutionSummary.Failure> failures = summary.getFailures();
        if (!failures.isEmpty())
            failures.forEach(failure -> LOGGER.warning("The following test case is failed:" + method.getName() + failure.getException()));
        else
            LOGGER.info("The following test case is passed: " + method.getName());

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
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return testsToRun;
    }

    private void findNewMethods() {
        ArrayList<SootMethod> sootEntryPoints = newProjectVersion.getEntryPoints();
        for (SootMethod sootTestMethod : sootEntryPoints) {
            Method testMethod = Util.findMethod(sootTestMethod.getName(), sootTestMethod.getDeclaringClass().getJavaStyleName(), sootTestMethod.getDeclaringClass().getPackageName(), newProjectVersion.getPath());
            if (!equalsMethodAndTheirTest.containsKey(testMethod) && !differentMethodAndTheirTest.containsKey(testMethod) && !othersMethodsNotPresentInOldProjectAndTheirTest.containsKey(testMethod)) {
                Iterator<Edge> e = newProjectVersion.getCallGraph().edgesOutOf(sootTestMethod);
                while (e.hasNext()) {
                    analyzeCallGraphForNewMethod(e.next(), sootTestMethod);
                }
            }
        }

    }


    private void analyzeCallGraphForNewMethod(Edge e, SootMethod entryPoint) {
        SootMethod newMethod = e.getTgt().method();
        if (!newMethod.isPhantom()) {
            Method test = Util.findMethod(entryPoint.getName(), entryPoint.getDeclaringClass().getJavaStyleName(), entryPoint.getDeclaringClass().getJavaPackageName(), newProjectVersion.getPath());
            addInMap(newMethod, test, othersMethodsNotPresentInOldProjectAndTheirTest);


        }
        Iterator<Edge> bho1 = newProjectVersion.getCallGraph().edgesOutOf(newMethod);
        Edge e3;

        while (bho1.hasNext()) {
            e3 = bho1.next();
            analyzeCallGraphForNewMethod(e3, entryPoint);
        }
    }

}




