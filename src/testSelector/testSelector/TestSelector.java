package testSelector.testSelector;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import testSelector.project.Project;
import testSelector.util.Util;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

public class TestSelector {


    private final HashMap<Method, ArrayList<String>> differentMethodAndTheirTest;
    private final HashMap<Method, ArrayList<String>> equalsMethodAndTheirTest;
    private final HashMap<Method, ArrayList<String>> othersMethodsNotPresentInOldProjectAndTheirTest;
    private final Project previousProjectVersion;
    private final Project newProjectVersion;
    private static final Logger LOGGER = Logger.getLogger(TestSelector.class.getName());

    public TestSelector(Project previousProjectVersion, Project newProjectVersion) {
        differentMethodAndTheirTest = new HashMap<Method, ArrayList<String>>();
        equalsMethodAndTheirTest = new HashMap<Method, ArrayList<String>>();
        othersMethodsNotPresentInOldProjectAndTheirTest = new HashMap<Method, ArrayList<String>>();
        this.previousProjectVersion = previousProjectVersion;
        this.newProjectVersion = newProjectVersion;
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
                    Method test = Util.findMethod(entryPoint.getName(), entryPoint.getDeclaringClass().getJavaStyleName(), entryPoint.getDeclaringClass().getJavaPackageName(), newProjectVersion.getPaths());
                    assert test != null;
                    if (Util.isJunitTestCase(test)) {
                        if (haveSameHashCode(m, m1) && haveSameParameter(m, m1)) { //hanno lo stesso hashcode:
                            if (!isDifferent(m, m1)) { //hanno lo stesso corpo
                                addInMap(m1, test, differentMethodAndTheirTest);
                            } else {
                                addInMap(m1, test, equalsMethodAndTheirTest);
                            }
                        } else {
                            //mi serve per beccare quei metodi nuovi toccati da un metodo di test che tocca un metodo diverso (stortura.)
                            //la pago però
                            //addInMap(m1, test, othersMethodsNotPresentInOldProjectAndTheirTest);
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


    public Set<Method> selectTest() {
        Iterator<SootMethod> it = newProjectVersion.getEntryPoints().iterator();
        ArrayList<Edge> yetAnalyzed = new ArrayList<>();
        while (it.hasNext()) {
            SootMethod method = it.next();
            Iterator<Edge> iteratorp1 = newProjectVersion.getCallGraph().edgesOutOf(method);
            while (iteratorp1.hasNext()) {
                callGraphsAnalyzer(iteratorp1.next(), method, yetAnalyzed);
            }
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

    private boolean haveSameParameter(SootMethod m, SootMethod m1) {
        return m.getSubSignature().equals(m1.getSubSignature());
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
            failures.forEach(failure -> failure.getException().printStackTrace());
            //        failures.forEach(failure ->  LOGGER.warning("The following test case is failed: " + failure.getTestIdentifier() +  "\n" + failure.getException().getMessage() + "\n"));
            // failure ->  LOGGER.warning("The following test case is failed: " + failure.getTestIdentifier() +  "\n" + failure.getException().getMessage() + "\n"));

        else if (summary.getTestsSucceededCount() >= 0)
            LOGGER.info("The following test case is passed: " + method.getName());

    }


    public Set<Method> runTestMethods() throws IllegalStateException {
        /*if (!isFindChangeCalled) {
            throw new IllegalStateException("You need to call before 'selectTest()' method ");
        }
        */

        Set<Method> testsToRun = getAllTestToRun();
        //  ClassPathUpdater.add(newProjectVersion.getPaths() + "/");
        for (Method testMethod : testsToRun) {
            runTestMethod(testMethod.getDeclaringClass(), testMethod);
        }
        return testsToRun;
    }

    //TODO; Se un test tocca 2 metodi: 1 nuovo e 1 modificato, il metodo nuovo non viene preso, è un problema di trasparenza, il test comunque verrebbe eseguito ma non darei corrette inso sul perchè quel metodo.
    private void findNewMethods() {
        ArrayList<SootMethod> sootEntryPoints = newProjectVersion.getEntryPoints();
        for (SootMethod sootTestMethod : sootEntryPoints) {
            Method testMethod = Util.findMethod(sootTestMethod.getName(), sootTestMethod.getDeclaringClass().getJavaStyleName(), sootTestMethod.getDeclaringClass().getPackageName(), newProjectVersion.getPaths());
            ArrayList<Edge> yetAnalyzed = new ArrayList<>();
            //  if (!equalsMethodAndTheirTest.containsKey(testMethod) && !differentMethodAndTheirTest.containsKey(testMethod) && !othersMethodsNotPresentInOldProjectAndTheirTest.containsKey(testMethod)) {
            Iterator<Edge> e = newProjectVersion.getCallGraph().edgesOutOf(sootTestMethod);
            while (e.hasNext()) {
                analyzeCallGraphForNewMethod(e.next(), sootTestMethod, yetAnalyzed);
                //  }
            }
        }

    }


    private void analyzeCallGraphForNewMethod(Edge e, SootMethod entryPoint, ArrayList<Edge> yetAnalyzed) {
        SootMethod newMethod = e.getTgt().method();
        if (!newMethod.isPhantom()) {
            AtomicBoolean isPresent = new AtomicBoolean(false);
            Collection<ArrayList<String>> equalsMethod = equalsMethodAndTheirTest.values();
            Collection<ArrayList<String>> differentMethod = getChangedMethods();
            for (ArrayList<String> stringArrayList : equalsMethod) {
                stringArrayList.forEach(s -> {
                    if (s.equals(newMethod.getName()))
                        isPresent.set(true);
                });
            }

            for (ArrayList<String> stringArrayList : differentMethod) {
                stringArrayList.forEach(s -> {
                    if (s.equals(newMethod.getName()))
                        isPresent.set(true);
                });
            }

            if (!isPresent.get()) {
                Method test = Util.findMethod(entryPoint.getName(), entryPoint.getDeclaringClass().getJavaStyleName(), entryPoint.getDeclaringClass().getJavaPackageName(), newProjectVersion.getPaths());
                addInMap(newMethod, test, othersMethodsNotPresentInOldProjectAndTheirTest);
            }

        }


        yetAnalyzed.add(e);
        Iterator<Edge> bho1 = newProjectVersion.getCallGraph().edgesOutOf(newMethod);
        Edge e3;


        while (bho1.hasNext()) {
            e3 = bho1.next();
            if (!yetAnalyzed.contains(e3))
                analyzeCallGraphForNewMethod(e3, entryPoint, yetAnalyzed);
        }
    }

}




