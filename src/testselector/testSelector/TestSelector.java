package testselector.testSelector;

import org.apache.log4j.Logger;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import testselector.project.Project;
import testselector.util.Util;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestSelector {


    private final Set<Test> differentMethodAndTheirTest;


    private final Set<Test> equalsMethodAndTheirTest;
    private final Set<Test> othersMethodsNotPresentInOldProjectAndTheirTest;
    private final Project previousProjectVersion;
    private final Project newProjectVersion;
    private static final Logger LOGGER = Logger.getLogger(TestSelector.class);

    public TestSelector(Project previousProjectVersion, Project newProjectVersion) {
        differentMethodAndTheirTest = new HashSet<>();
        equalsMethodAndTheirTest = new HashSet<>();
        othersMethodsNotPresentInOldProjectAndTheirTest = new HashSet<>();
        this.previousProjectVersion = previousProjectVersion;
        this.newProjectVersion = newProjectVersion;
    }

    public Set<Test> getDifferentMethodAndTheirTest() {
        return differentMethodAndTheirTest;
    }

    public Set<Test> getOthersMethodsNotPresentInOldProjectAndTheirTest() {
        return othersMethodsNotPresentInOldProjectAndTheirTest;
    }

    public Collection<Set<String>> getChangedMethods() {
        Collection<Set<String>> testingMethods = new ArrayList<>();
        differentMethodAndTheirTest.forEach(test -> testingMethods.add(test.getTestingMethods()));
        return testingMethods;
    }

    public Collection<Set<String>> getNewOrRemovedMethods() {

        Collection<Set<String>> testingMethods = new ArrayList<>();
        othersMethodsNotPresentInOldProjectAndTheirTest.forEach(test -> testingMethods.add(test.getTestingMethods()));
        return testingMethods;
    }

    private Collection<Set<String>> getEqualsMethods() {

        Collection<Set<String>> testingMethods = new ArrayList<>();
        equalsMethodAndTheirTest.forEach(test -> testingMethods.add(test.getTestingMethods()));
        return testingMethods;
    }


    public Set<Test> getAllTestToRun() {
        Set<Test> allTest = new HashSet<>();
        allTest.addAll(getDifferentMethodAndTheirTest());
        allTest.addAll(getOthersMethodsNotPresentInOldProjectAndTheirTest());
        return allTest;
    }

    private void callGraphsAnalyzer(Edge e, SootMethod entryPoint, ArrayList<Edge> yetAnalyzed) {
        if (!yetAnalyzed.contains(e)) {
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
                                    //stampa troppe volte, ogni qual volta matcha.
                                    //dovrebbe stampare una volta per metodo.
                                    if (!isIn(test, differentMethodAndTheirTest).get()) {
                                        LOGGER.info("Found change in this method:" +
                                                " " + m.getDeclaringClass() + "." + m.getName() + " "
                                                + "tested by: " + entryPoint.getDeclaringClass() + "." + entryPoint.getName());
                                    }
                                    addInMap(m1, test, differentMethodAndTheirTest);
                                    break;

                                } else {
                                    addInMap(m1, test, equalsMethodAndTheirTest);
                                    break;

                                }
                            }

                        }

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

    private void addInMap(SootMethod m1, Method test, Set<Test> hashMap) {
        AtomicBoolean is = isIn(test, hashMap);


        if (!is.get()) {
            HashSet<String> ts = new HashSet<>();
            ts.add(m1.getName());
            hashMap.add(new Test(test, ts));
        } else {

            hashMap.forEach((Test test1) ->
            {
                if (test1.getTestMethod().equals(test)) {
                    is.set(true);
                    test1.addTestingMethod(m1.getName());
                }

            });
        }


    }

    private AtomicBoolean isIn(Method test, Set<Test> hashMap) {
        AtomicBoolean is = new AtomicBoolean(false);
        hashMap.forEach((Test test1) ->
        {
            if (test1.getTestMethod().equals(test)) {
                is.set(true);
            }

        });
        return is;
    }


    public Set<Test> selectTest() {
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



    private void findNewMethods() {
        List<SootMethod> sootEntryPoints = newProjectVersion.getEntryPoints();
        for (SootMethod sootTestMethod : sootEntryPoints) {
            ArrayList<Edge> yetAnalyzed = new ArrayList<>();
            Iterator<Edge> e = newProjectVersion.getCallGraph().edgesOutOf(sootTestMethod);
            while (e.hasNext()) {
                analyzeCallGraphForNewMethod(e.next(), sootTestMethod, yetAnalyzed);
            }
        }

    }


    private void analyzeCallGraphForNewMethod(Edge e, SootMethod entryPoint, ArrayList<Edge> yetAnalyzed) {
        SootMethod newMethod = e.getTgt().method();
        if (!newMethod.isPhantom()) {
            AtomicBoolean isPresent = new AtomicBoolean(false);
            Collection<Set<String>> equalsMethod = getEqualsMethods();
            Collection<Set<String>> differentMethod = getChangedMethods();

            for (Set<String> stringArrayList : equalsMethod) {
                stringArrayList.forEach(s -> {
                    if (s.equals(newMethod.getName()))
                        isPresent.set(true);
                });
            }

            for (Set<String> stringArrayList : getNewOrRemovedMethods()) {
                stringArrayList.forEach(s -> {
                    if (s.equals(newMethod.getName()))
                        isPresent.set(true);
                });
            }

            for (Set<String> stringArrayList : differentMethod) {
                stringArrayList.forEach(s -> {
                    if (s.equals(newMethod.getName()))
                        isPresent.set(true);
                });
            }

            if (!isPresent.get()) {
                Method test = Util.findMethod(entryPoint.getName(), entryPoint.getDeclaringClass().getJavaStyleName(), entryPoint.getDeclaringClass().getJavaPackageName(), newProjectVersion.getPaths());
                LOGGER.info("Found new  method:" +
                        " " + newMethod.getDeclaringClass() + "." + newMethod.getName() + " "
                        + "tested by: " + entryPoint.getDeclaringClass() + "." + entryPoint.getName());
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




