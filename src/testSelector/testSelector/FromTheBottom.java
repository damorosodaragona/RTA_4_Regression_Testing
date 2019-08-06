package testSelector.testSelector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.toolkits.callgraph.Edge;
import testSelector.project.NewProject;
import testSelector.project.PreviousProject;
import testSelector.project.Project;
import testSelector.util.Util;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FromTheBottom {

    private final boolean alsoNew;
    private final Set<Test> differentMethodAndTheirTest;
    private final Set<Test> equalsMethodAndTheirTest;
    private final Set<Test> newMethodsAndTheirTest;
    private final Set<SootClass> differentObject;
    private final Set<Test> methodsToRunForDifferenceInObject;
    private final Set<Test> differentTest;
    private final HashSet<SootMethod> deletedMethods;
    private final Set<SootMethod> allMethodsAnalyzed;
    private final PreviousProject previousProjectVersion;
    private final NewProject newProjectVersion;
    private static final Logger LOGGER = Logger.getLogger(Main.class);
    private ArrayList<Edge> differenteEdge;
    private HashSet<SootMethod> differentMethods;
    private HashSet<SootMethod> newMethods;

    private HashSet<SootMethod> equalsMethods;

    public Integer count;

    public Set<SootMethod> getAllMethodsAnalyzed() {
        return allMethodsAnalyzed;
    }

    /**
     * @param previousProjectVersion the old project version
     * @param newProjectVersion      the new project version
     * @param alsoNew
     */
    public FromTheBottom(Project previousProjectVersion, Project newProjectVersion, boolean alsoNew) throws testselector.exception.NoTestFoundedException {
        this.methodsToRunForDifferenceInObject = new HashSet<>();
        this.differentObject = new HashSet<>();
        this.differentMethodAndTheirTest = new HashSet<>();
        this.equalsMethodAndTheirTest = new HashSet<>();
        this.newMethodsAndTheirTest = new HashSet<>();
        this.differentTest = new HashSet<>();
        this.previousProjectVersion = (PreviousProject) previousProjectVersion;
        this.newProjectVersion = (NewProject) newProjectVersion;
        this.alsoNew = alsoNew;
        this.differenteEdge = new ArrayList<>();
        this.differentMethods = new HashSet<>();
        this.count = 0;
        this.allMethodsAnalyzed = new HashSet<>();
        this.deletedMethods = new HashSet<>();
        this.equalsMethods = new HashSet<>();
        this.newMethods = new HashSet<>();
        LOGGER.setLevel(Level.DEBUG);
    }


    /**
     * Get a set with test that test methods different from the old version project
     *
     * @return a set with test that test method different from the old version project
     */
    public Set<Test> getDifferentMethodAndTheirTest() {
        return differentMethodAndTheirTest;
    }

    /**
     * Get a set with tests that test new methods, so the methods that aren't in the old project version
     *
     * @return a set with tests that test new methods
     */
    public Set<Test> getNewMethodsAndTheirTest() {
        return newMethodsAndTheirTest;
    }

    /**
     * Get a string collection with the name of the methods that are dfferent from the old project version
     *
     * @return a collection with the java style name (package.classname) of the methods that are different from the old project version
     */
    public Collection<Set<String>> getChangedMethods() {
        Collection<Set<String>> testingMethods = new ArrayList<>();
        differentMethodAndTheirTest.forEach(test -> testingMethods.add(test.getTestingMethods()));
        return testingMethods;
    }

    /**
     * Get a string collection with the name of the methods that are new, so that aren't in the old project version
     *
     * @return a collection with the java style name (package.classname) of the methods that are new
     */
    public Collection<Set<String>> getNewOrRemovedMethods() {

        Collection<Set<String>> testingMethods = new ArrayList<>();
        newMethodsAndTheirTest.forEach(test -> testingMethods.add(test.getTestingMethods()));
        return testingMethods;
    }

    /**
     * Get a string collection with the name of the methods that are equal in the both of project version
     *
     * @return a collection with the java style name (package.classname) of the methods that are equal
     */
    private Collection<Set<String>> getEqualsMethods() {

        Collection<Set<String>> testingMethods = new ArrayList<>();
        equalsMethodAndTheirTest.forEach(test -> testingMethods.add(test.getTestingMethods()));
        return testingMethods;
    }

    /**
     * Get all test that are necessary to run for the new project version.
     * If the option -new is enable this test return also the test that test the new methods in the new version of the project,
     * else for default return only the test that test the method that are different in the two version of the projcet.
     * If there is an object that have some difference in the constructor this method return all test that test the method of that class.
     *
     * @return a set of Test with all test that are necessary to run for the new project version.
     */
    public synchronized Set<Test> getAllTestToRun() {

        Set<Test> allTest = new HashSet<>();
        allTest.addAll(getDifferentMethodAndTheirTest());
        allTest.addAll(getNewMethodsAndTheirTest());
        allTest.addAll(methodsToRunForDifferenceInObject);
        allTest.addAll(differentTest);
        return allTest;
    }


    /**
     * Get all test that are necessary to run for the new project version.
     *
     * @return a set of Test with all test that are necessary to run for the new project version.
     */
    public Set<Test> selectTest() throws testselector.exception.NoTestFoundedException {

        PackManager.v().runPacks();


        findDifferenceInHierarchy();


        findDifferentMethods();
        findNewMethods();
        LOGGER.info("comparing the two test suite to see if there are differents tests");
        comparingTest();
        LOGGER.info("comparing the two classes to see if the constructors are equals");
        isTheSameObject();

        newProjectVersion.createCallgraph();

        previousProjectVersion.moveToAnotherPackage(newProjectVersion.getMovedToAnotherPackage());


        first(differentMethods, differentMethodAndTheirTest);
        first(newMethods, newMethodsAndTheirTest);
        for (SootClass s : differentObject) {
            first(new HashSet<>(s.getMethods()), methodsToRunForDifferenceInObject);
        }

        return getAllTestToRun();
    }

    private void findDifferenceInHierarchy() {
        ArrayList<SootMethod> differentHierarchy = new ArrayList<>();
        ArrayList<SootMethod> deletedMethods = new ArrayList<>();
        for (SootMethod m : previousProjectVersion.getApplicationMethod()) {
            boolean isIn = false;
            for (SootMethod m1 : newProjectVersion.getApplicationMethod()) {
                if (m.getSignature().equals(m1.getSignature()))
                    isIn = true;
            }
            if (!isIn) {
                deletedMethods.add(m);

            }

        }

        for (SootMethod deleted : deletedMethods) {
            for (SootClass subClass : previousProjectVersion.getHierarchy().getSubclassesOf(deleted.getDeclaringClass())) {
                for (SootMethod override : subClass.getMethods()) {
                    if (override.getSubSignature().equals(deleted.getSubSignature()))
                        differentHierarchy.add(override);
                }
            }

            for (SootClass subClass : previousProjectVersion.getHierarchy().getSuperclassesOf(deleted.getDeclaringClass())) {
                for (SootMethod override : subClass.getMethods()) {
                    if (override.getSubSignature().equals(deleted.getSubSignature()))
                        differentHierarchy.add(override);
                }
            }
        }

        for (SootMethod toMarkBecauseCallDeleteMethods : newProjectVersion.getApplicationMethod()) {
            for (SootMethod methodDifferentInHierarchy : differentHierarchy) {


                if (methodDifferentInHierarchy.getSignature().equals(toMarkBecauseCallDeleteMethods.getSignature())) {
                    LOGGER.info("The method: " + toMarkBecauseCallDeleteMethods.getDeclaringClass().getName() + "." + toMarkBecauseCallDeleteMethods.getName() + " has been marked has modified because the method in his hierarchy " + methodDifferentInHierarchy.getDeclaringClass() + "." + methodDifferentInHierarchy.getName() + " has been deleted");
                    differentMethods.add(toMarkBecauseCallDeleteMethods);
                }
            }
        }


    }

    private void findDifferentMethods() {
        Date start = new Date();

        LOGGER.debug("start find different methods at " + start.getTime());
        HashSet<SootClass> p1Class = newProjectVersion.getProjectClasses();
        HashSet<SootClass> copyPClass = previousProjectVersion.getProjectClasses();
        for (SootClass s1 : p1Class) {
            SootClass classToRemove;
            List<SootClass> pClass = new ArrayList<>(copyPClass);
            for (SootClass s : pClass) {
                if (s.getName().equals(s1.getName())) {
                    classToRemove = s;
                    List<SootMethod> ms1 = s1.getMethods();
                    for (SootMethod m1 : ms1) {
                        if (Modifier.isAbstract(m1.getModifiers()))
                            continue;
                        for (SootMethod m : s.getMethods()) {
                            if (haveSameParameter(m, m1) && m.getName().equals(m1.getName())) {
                                if (!isEquals(m, m1)) {

                                    differentMethods.add(m1);
                                } else
                                    equalsMethods.add(m1);

                                break;
                            }
                        }
                    }
                    copyPClass.remove(classToRemove);
                    break;
                }
            }

        }
        start = new Date();
        LOGGER.debug("finish find different methods at " + start.getTime());

    }


    /*
     * Compare every test in the two versions of the project. ù
     * If there is a test method with the same name, in the same class and in the same package in the
     * both versions of the project this method is compared and if it's not equals is selected regardless
     * of the methods it tests.
     */
    private void comparingTest() {


        Iterator<SootMethod> it = differentMethods.iterator();
        while (it.hasNext()) {
            SootMethod testMethod = it.next();
            if (Util.isATestMethod(testMethod, newProjectVersion.getJunitVersion())) {
                if (Util.isSetup(testMethod, newProjectVersion.getJunitVersion())) {
                    for (SootMethod s : testMethod.getDeclaringClass().getMethods()) {
                        //aggiungo ai test differenti solo i test -> metodi con @Test. I @Before,@After ecc ecc verrano eseguiti lo stesso
                        if (Util.isJunitTestCase(s, newProjectVersion.getJunitVersion())) {

                            boolean isIn = false;
                            for (Test t : differentTest) {
                                if (t.getTestMethod().equals(s))
                                    isIn = true;
                            }

                            if (!isIn) {
                                LOGGER.info("The test: " + s.getDeclaringClass().getName() + "." + s.getName() + " has been added because it is in both versions of the project but has been changed");
                                differentTest.add(new Test(s));
                            }
                        }
                    }

                } else {
                    //aggiungo ai test differenti solo i test -> metodi con @Test. I @Before,@After ecc ecc verrano eseguiti lo stesso
                    if (Util.isJunitTestCase(testMethod, newProjectVersion.getJunitVersion())) {
                        LOGGER.info("The test: " + testMethod.getDeclaringClass().getName() + "." + testMethod.getName() + " has been added because it is in both versions of the project but has been changed");
                        differentTest.add(new Test(testMethod));
                    }

                }
                //entryPointsToRemove.add(testMethod);
            }
        }
    }

    /*
    This method check if all the object in both project are the same.
    if it'snt, so there are differences in constructor (different fields, different variables, different constants)
    all tests with a reference to that onbect are selecting
     */
    private void isTheSameObject() {

        differentMethods.forEach(sootMethod -> {
            if (sootMethod.getName().startsWith("<clinit>"))
                differentObject.add(sootMethod.getDeclaringClass());
        });


    }



    /*
    Analyze the callgraph starting from a entry point, so from a test and going down the arches.
    So the analysis start from a test and go over all the methods that the test tests.
    For each method find the corrispettive methods in the old version and check if there are difference,
    in thi case save the test and that methods in a list. (so the test are selecting)

     test -> a -> d -> e
          -> b
          -> c
     So start to analyze "a", than analyze d than analyze e.
     (the method "selectTest" than recall this method with parameter: edge node [test -> b], entrypoint [test], yetanalyzed [test -> a,  a -> d . d -> e] )
     */


    private boolean callGraphsAnalyzer(Edge e1, ArrayList<Edge> yetAnalyzed, SootMethod entryPoint) {

        SootMethod srcM1 = e1.src();
        SootMethod tgtM1 = e1.tgt();

        boolean continueThisSubCallGraph = true;

        if (!isDifferentObject(entryPoint, srcM1) && !isDifferentObject(entryPoint, tgtM1)) {

            if (!yetAnalyzed.contains(e1)) {

                differenteEdge.remove(e1);

                if (tgtM1.getDeclaringClass().isApplicationClass()) {
                    if (differentMethods.contains(tgtM1)) {
                        synchronized (LOGGER) {
                            LOGGER.info("Found change in this method:" +
                                    " " + tgtM1.getDeclaringClass() + "." + tgtM1.getName() + " "
                                    + "tested by: " + entryPoint.getDeclaringClass() + "." + entryPoint.getName());
                        }
                        synchronized (differentMethodAndTheirTest) {
                            addInMap(tgtM1, entryPoint, differentMethodAndTheirTest);
                        }
                        continueThisSubCallGraph = false;

                    } else if (newMethods.contains(tgtM1)) {
                        synchronized (LOGGER) {
                            LOGGER.info("Found new method:" +
                                    " " + tgtM1.getDeclaringClass() + "." + tgtM1.getName() + " "
                                    + "tested by: " + entryPoint.getDeclaringClass() + "." + entryPoint.getName());
                        }
                        synchronized (newMethodsAndTheirTest) {
                            addInMap(tgtM1, entryPoint, newMethodsAndTheirTest);
                        }
                        continueThisSubCallGraph = false;

                    }
                }

                // }
                yetAnalyzed.add(e1);


                //retrieve a method from the node (the method at the end so i a node contain a that call b, retrieve b)
                SootMethod targetM1Method = e1.getTgt().method();

                //get an iterator over the arches that going out from that method
                Iterator<Edge> archesFromTargetM1Method = newProjectVersion.getCallGraph().edgesOutOf(targetM1Method);

                Edge edgeP1;
                //retrieve a method from the node (the method at the end so i a node contain a that call b, retrieve b)
                //get an iterator over the arches that going out from that method
                //while the method are arches
                while (archesFromTargetM1Method.hasNext() && continueThisSubCallGraph) {
                    edgeP1 = archesFromTargetM1Method.next();
                    //retieve the node
                    //if the node are not analyzed yet
                    //recall this function with the new node, same entypoints and the list of the node analyzed yet.
                    continueThisSubCallGraph = callGraphsAnalyzer(edgeP1, yetAnalyzed, entryPoint);


                }

            }
        } else
            continueThisSubCallGraph = false;
        return continueThisSubCallGraph;
    }


    private boolean isDifferentObject(SootMethod entryPoint, SootMethod sootMethod) {
        boolean isDifferentObject = false;
        if (differentObject.contains(sootMethod.getDeclaringClass())) {
            synchronized (LOGGER) {
                LOGGER.info("Added test:" +
                        " " + entryPoint.getDeclaringClass() + "." + entryPoint.getName() + " that test method " + sootMethod.getDeclaringClass() + "." + sootMethod.getName() + " " +
                        "because the constructor or fields of class " + sootMethod.getDeclaringClass() + " is different from the previous version");
            }
            //add in a list this method and it test
            synchronized (methodsToRunForDifferenceInObject) {
                addInMap(sootMethod, entryPoint, methodsToRunForDifferenceInObject);
            }
            //unmark the method and it test as new.
            //removeToMap(srcM1, test, newMethodsAndTheirTest);
            isDifferentObject = true;
        }
        return isDifferentObject;
    }


    private void addInMap(SootMethod m1, SootMethod test, Set<Test> hashMap) {
        AtomicBoolean is = isIn(test, hashMap);
        //  Method method = Util.findMethod(m1.getName(), m1.getDeclaringClass().getJavaStyleName(), m1.getDeclaringClass().getJavaPackageName(), newProjectVersion.getTarget());
        //java don't permit to retrieve the <clinit> and <init> method, but these are in callgraph, so when we try to retrieve this method
        //java return an error. So we must check if the method are null.
        // if (method != null) {
        if (!is.get()) {
            HashSet<String> ts = new HashSet<>();
            ts.add(m1.getDeclaringClass().getName() + "." + m1.getName());
            hashMap.add(new Test(test, ts));
        } else {

            hashMap.forEach((Test test1) ->
            {
                if (test1.getTestMethod().equals(test)) {
                    test1.addTestingMethod(m1.getDeclaringClass().getName() + "." + m1.getName());
                }

            });
        }
        //  }

    }

    private AtomicBoolean isIn(SootMethod test, Set<Test> hashMap) {
        AtomicBoolean is = new AtomicBoolean(false);
        hashMap.forEach((Test test1) ->
        {
            if (test1.getTestMethod().equals(test)) {
                is.set(true);
            }

        });
        return is;
    }


    private boolean isEquals(SootMethod m, SootMethod m1) {
        return m.getActiveBody().toString().equals(m1.getActiveBody().toString());
    }


    private boolean haveSameParameter(SootMethod m, SootMethod m1) {
        return m.getSubSignature().equals(m1.getSubSignature());
    }


    private void findNewMethods() {
        newMethods.addAll(newProjectVersion.getApplicationMethod());
        newMethods.removeAll(differentMethods);
        newMethods.removeAll(equalsMethods);

    }

    public void first(HashSet<SootMethod> hashset, Set<Test> mapInToAdd) {
        for (SootMethod m : hashset) {
            Iterator<Edge> iterator = newProjectVersion.getCallGraph().edgesInto(m);
            ArrayList<Edge> yetAnalyzed = new ArrayList<>();
            while (iterator.hasNext()) {
                Edge e = iterator.next();
                run1(e, m, yetAnalyzed, mapInToAdd);
            }

        }
    }

    public void run1(Edge e, SootMethod m, ArrayList<Edge> yetAnalyzed, Set<Test> mapInToAdd) {

        allMethodsAnalyzed.add(e.src());
        if (!newProjectVersion.getEntryPoints().contains(e.src())) {
            if (Util.isJunitTestCase(e.src(), newProjectVersion.getJunitVersion())) {
                addInMap(m, e.src(), mapInToAdd);
                return;

            }
        }
        if (yetAnalyzed.contains(e))
            return;

        yetAnalyzed.add(e);


        //retrieve a method from the node (the method at the end so i a node contain a that call b, retrieve b)
        SootMethod targetM1Method = e.getSrc().method();

        //get an iterator over the arches that going out from that method
        Iterator<Edge> archesFromTargetM1Method = newProjectVersion.getCallGraph().edgesInto(targetM1Method);

        Edge edgeP1;
        //retrieve a method from the node (the method at the end so i a node contain a that call b, retrieve b)
        //get an iterator over the arches that going out from that method
        //while the method are arches
        while (archesFromTargetM1Method.hasNext()) {
            edgeP1 = archesFromTargetM1Method.next();
            //retieve the node
            //if the node are not analyzed yet
            //recall this function with the new node, same entypoints and the list of the node analyzed yet.
            run1(edgeP1, m, yetAnalyzed, mapInToAdd);

        }

    }

    private class Analyzer extends Thread {
        private SootMethod[] sootMethodM1;

        public Analyzer(SootMethod... sootMethodM1) {
            this.sootMethodM1 = sootMethodM1;

        }


        @Override
        public void run() {

            for (SootMethod m : sootMethodM1) {
                Iterator<Edge> iterator = newProjectVersion.getCallGraph().edgesOutOf(m);
                while (iterator.hasNext()) {
                    Edge e = iterator.next();
                    boolean continueThisSubCallgraph = true;

                    for (Test t : differentTest) {
                        if (t.getTestMethod().equals(e.tgt()))
                            continueThisSubCallgraph = false;
                    }
                    if (Util.isJunitTestCase(e.tgt(), newProjectVersion.getJunitVersion()) && continueThisSubCallgraph) {
                        ArrayList<Edge> yetAnalyzed = new ArrayList<>();
                        synchronized (LOGGER) {
                            LOGGER.info("Analyzing: " + e.tgt().getDeclaringClass() + "." + e.tgt().getName());
                        }
                        synchronized (count) {
                            count++;
                        }
                        synchronized (allMethodsAnalyzed) {
                            allMethodsAnalyzed.add(e.tgt());
                        }
                        Iterator<Edge> iteratorP1 = newProjectVersion.getCallGraph().edgesOutOf(e.tgt());
                        while (iteratorP1.hasNext() && continueThisSubCallgraph) {
                            Edge e1 = iteratorP1.next();
                            //if (newProjectVersion.getProjectClasses().contains(e1.getTgt().method().getDeclaringClass())) {
                            continueThisSubCallgraph = callGraphsAnalyzer(e1, yetAnalyzed, e.tgt());
                            //}
                        }
                    }

                }


            }

        }


    }

}








