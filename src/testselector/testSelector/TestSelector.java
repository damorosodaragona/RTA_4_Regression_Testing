//package testselector.testSelector;
//
//import org.apache.log4j.Logger;
//import soot.SootClass;
//import soot.SootMethod;
//import soot.jimple.toolkits.callgraph.CallGraph;
//import soot.jimple.toolkits.callgraph.Edge;
//import testselector.main.Main;
//import testselector.project.Project;
//import testselector.util.Util;
//
//import java.lang.reflect.Method;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class TestSelector {
//
//    private final boolean alsoNew;
//    private final Set<Test> differentMethodAndTheirTest;
//    private final Set<Test> equalsMethodAndTheirTest;
//    private final Set<Test> newMethodsAndTheirTest;
//    private final Set<SootClass> differentObject;
//    private final Set<Test> methodsToRunForDifferenceInObject;
//    private final Set<Test> differentTest;
//
//    private final Project previousProjectVersion;
//    private final Project newProjectVersion;
//    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
//
//
//    /**
//     * @param previousProjectVersion the old project version
//     * @param newProjectVersion      the new project version
//     * @param alsoNew
//     */
//    public TestSelector(Project previousProjectVersion, Project newProjectVersion, boolean alsoNew) {
//        this.methodsToRunForDifferenceInObject = new HashSet<>();
//        this.differentObject = new HashSet<>();
//        this.differentMethodAndTheirTest = new HashSet<>();
//        this.equalsMethodAndTheirTest = new HashSet<>();
//        this.newMethodsAndTheirTest = new HashSet<>();
//        this.differentTest = new HashSet<>();
//        this.previousProjectVersion = previousProjectVersion;
//        this.newProjectVersion = newProjectVersion;
//        this.alsoNew = alsoNew;
//    }
//
//    /**
//     * Get a set with test that test methods different from the old version project
//     *
//     * @return a set with test that test method different from the old version project
//     */
//    public Set<Test> getDifferentMethodAndTheirTest() {
//        return differentMethodAndTheirTest;
//    }
//
//    /**
//     * Get a set with tests that test new methods, so the methods that aren't in the old project version
//     *
//     * @return a set with tests that test new methods
//     */
//    public Set<Test> getNewMethodsAndTheirTest() {
//        return newMethodsAndTheirTest;
//    }
//
//    /**
//     * Get a string collection with the name of the methods that are dfferent from the old project version
//     *
//     * @return a collection with the java style name (package.classname) of the methods that are different from the old project version
//     */
//    //TODO: SNELLIRE IL CODICE? QUI CREO UN ARRAYLIST DI ARRAYLIST. POTREI CREARE UN ARRYLIST IN MODO DA RENDERE PIù USABILE IL CODICE.
//    public Collection<Set<Method>> getChangedMethods() {
//        Collection<Set<Method>> testingMethods = new ArrayList<>();
//        differentMethodAndTheirTest.forEach(test -> testingMethods.add(test.getTestingMethods()));
//        return testingMethods;
//    }
//
//    /**
//     * Get a string collection with the name of the methods that are new, so that aren't in the old project version
//     *
//     * @return a collection with the java style name (package.classname) of the methods that are new
//     */
//    public Collection<Set<Method>> getNewOrRemovedMethods() {
//
//        Collection<Set<Method>> testingMethods = new ArrayList<>();
//        newMethodsAndTheirTest.forEach(test -> testingMethods.add(test.getTestingMethods()));
//        return testingMethods;
//    }
//
//    /**
//     * Get a string collection with the name of the methods that are equal in the both of project version
//     *
//     * @return a collection with the java style name (package.classname) of the methods that are equal
//     */
//    private Collection<Set<Method>> getEqualsMethods() {
//
//        Collection<Set<Method>> testingMethods = new ArrayList<>();
//        equalsMethodAndTheirTest.forEach(test -> testingMethods.add(test.getTestingMethods()));
//        return testingMethods;
//    }
//
//    /*
//     * Get all test that are necessary to run for the new project version.
//     * If the option -new is enable this test return also the test that test the new methods in the new version of the project,
//     * else for default return only the test that test the method that are different in the two version of the projcet.
//     * If there is an object that have some difference in the constructor this method return all test that test the method of that class.
//     * @return a set of Test with all test that are necessary to run for the new project version.
//     */
//    private Set<Test> getAllTestToRun() {
//        Set<Test> allTest = new HashSet<>();
//        allTest.addAll(getDifferentMethodAndTheirTest());
//        allTest.addAll(getNewMethodsAndTheirTest());
//        allTest.addAll(methodsToRunForDifferenceInObject);
//        allTest.addAll(differentTest);
//        return allTest;
//    }
//
//
//    /**
//     * Get all test that are necessary to run for the new project version.
//     * If the option -new is enable this test return also the test that test the new methods in the new version of the project,
//     * else for default return only the test that test the method that are different in the two version of the projcet.
//     * If there is an object that have some difference in the constructor this method return all test that test the method of that class.
//     *
//     * @return a set of Test with all test that are necessary to run for the new project version.
//     */
//    public Set<Test> selectTest() {
//        LOGGER.info("comparing the two classes to see if the constructors are equals");
//        isTheSameObject();
//        LOGGER.info("comparing the two test suirte to see if there are differents tests");
//        comparingTest();
//        LOGGER.info("starting comparing callgraph");
//
//        Iterator<SootMethod> it = newProjectVersion.getEntryPoints().iterator();
//        ArrayList<Edge> yetAnalyzed = new ArrayList<>();
//        while (it.hasNext()) {
//            SootMethod method = it.next();
//            Method test = Util.findMethod(method.getName(), method.getDeclaringClass().getJavaStyleName(), method.getDeclaringClass().getJavaPackageName(), newProjectVersion.getTarget());
//            Iterator<Edge> iteratorp1 = newProjectVersion.getCallGraph().edgesOutOf(method);
//            LOGGER.info("Analyzing:" + method.getDeclaringClass().getJavaStyleName() + "." + method.getName());
//            if (test != null) {
//                while (iteratorp1.hasNext()) {
//                    callGraphsAnalyzer(iteratorp1.next(), method, yetAnalyzed, test);
//                }
//            }else{
//                LOGGER.info("Can't retrieve:" + method.getDeclaringClass().getJavaStyleName() + "." + method.getName());
//
//            }
//        }
//        newMethodsAndTheirTest.forEach(test -> test.getTestingMethods().forEach(s -> {
//                    LOGGER.info("Found new method:" +
//                            " " + "." + s.getDeclaringClass().getName() + "." + s.getName()
//                            + " tested by: " + test.getTestMethod().getDeclaringClass().getName() + "." + test.getTestMethod().getName());
//                }
//        ));
//        //if (alsoNew)
//        //  findNewMethods();
//        return getAllTestToRun();
//    }
//
//    /*
//     * Compare every test in the two versions of the project. ù
//     * If there is a test method with the same name, in the same class and in the same package in the
//     * both versions of the project this method is compared and if it's not equals is selected regardless
//     * of the methods it tests.
//     */
//    private void comparingTest() {
//        List<SootMethod> pTests = new ArrayList<>(previousProjectVersion.getEntryPoints());
//        List<SootMethod> p1Tests = new ArrayList<>(newProjectVersion.getEntryPoints());
//        for (SootMethod sootMethod1 : p1Tests) {
//            for (SootMethod sootMethod : pTests) {
//                if (haveSameNameAndAreInSamePackage(sootMethod, sootMethod1)) {
//                    if (haveSameParameter(sootMethod, sootMethod1)) {
//                        if (!isEquals(sootMethod, sootMethod1)) {
//                            newProjectVersion.removeEntryPoint(sootMethod1);
//                            previousProjectVersion.removeEntryPoint(sootMethod);
//                            differentTest.add(new Test(Util.findMethod(sootMethod1.getName(), sootMethod1.getDeclaringClass().getJavaStyleName(), sootMethod1.getDeclaringClass().getPackageName(), newProjectVersion.getTarget())));
//                            LOGGER.info("The test: " + sootMethod1.getDeclaringClass().getName() + "." + sootMethod1.getName() + " has been added because it is in both versions of the project but has been changed");
//                        }
//                    }
//                }
//            }
//
//        }
//
//    }
//
//
//    /*
//    This method check if all the object in both project are the same.
//    if it'snt, so there are differences in constructor (different fields, different variables, different constants)
//    all tests with a reference to that onbect are selecting
//     */
//    private boolean isTheSameObject() {
//        //check variable
//        AtomicBoolean check = new AtomicBoolean(true);
//        //for all the classes in the new version of the project
//        newProjectVersion.getProjectClasses().forEach(sootClass1 -> {
//            //if the class is not a test class
//            if (!newProjectVersion.getTestingClass().containsKey(sootClass1)) {
//                //for all the classes in the old version of the project
//                previousProjectVersion.getProjectClasses().forEach(sootClass -> {
//                    //if the class is not a test class
//                    if (!previousProjectVersion.getTestingClass().containsKey(sootClass)) {
//                        //if the class have the same name and is the same package
//                        if (sootClass1.getJavaStyleName().equals(sootClass.getJavaStyleName())) {
//                            //retrieve the init method for the new version of the class
//                            // SootMethod mInit1 = sootClass1.getMethodByName("<init>");
//                            List<SootMethod> methods1 = new ArrayList<>(sootClass1.getMethods());
//                            ArrayList<SootMethod> initMethods1Finded = new ArrayList<>();
//                            methods1.forEach(sootMethod1 -> {
//                                if (sootMethod1.getName().startsWith("<init"))
//                                    initMethods1Finded.add(sootMethod1);
//                            });
//                            List<SootMethod> methods = new ArrayList<>(sootClass.getMethods());
//                            ArrayList<SootMethod> initMethodsFinded = new ArrayList<>();
//                            methods.forEach(sootMethod -> {
//                                if (sootMethod.getName().startsWith("<init"))
//                                    initMethodsFinded.add(sootMethod);
//                            });
//
//
//                            //SootMethod mInit1 =  sootClass1.getMethod("void <init>()");
//                            //retrieve the init method for the old version of the class
//                            //     SootMethod mInit = sootClass.getMethodByName("<init>");
//                            //SootMethod mInit =  sootClass.getMethod("void <init>()");
//
//                            SootMethod mClinit1;
//                            SootMethod mClinit;
//                            try {
//                                //try to retrieve the clinit method for the new version of the class
//                                mClinit1 = sootClass1.getMethodByName("<clinit>");
//                                //try to retrieve the clinit method for the new version of the class
//                                mClinit = sootClass.getMethodByName("<clinit>");
//                                //if the clinit or the init method is different
//                                if (!isEquals(mClinit1, mClinit)) {
//                                    //set false the check variable
//                                    check.set(false);
//                                    //add this class in a list
//                                    differentObject.add(sootClass1);
//                                }
//                                //if the class don't have a clinit method
//                            } catch (RuntimeException e) {
//                            } finally {
//                                //check if the init method are equal if not
//                                if (check.get()) {
//                                    initMethods1Finded.forEach(initMethod1 -> {
//                                        initMethodsFinded.forEach(initMethod -> {
//                                            if (initMethod.getName().equals(initMethod1.getName()) && initMethod.getSignature().equals(initMethod1.getSignature())) {
//                                                if (!isEquals(initMethod, initMethod1)) {
//                                                    check.set(false);
//                                                    differentObject.add(sootClass1);
//                                                }
//                                            }
//
//                                        });
//                                    });
//                                }
//
//                            }
//                        }
//                    }
//                });
//            }
//        });
//        //return the value of the check variable. True if all object are the same in both version, false if not.
//        return check.get();
//    }
//
//
//    /*
//    Analyze the callgraph starting from a entry point, so from a test and going down the arches.
//    So the analysis start from a test and go over all the methods that the test tests.
//    For each method find the corrispettive methods in the old version and check if there are difference,
//    in thi case save the test and that methods in a list. (so the test are selecting)
//
//     test -> a -> d -> e
//          -> b
//          -> c
//     So start to analyze "a", than analyze d than analyze e.
//     (the method "selectTest" than recall this method with parameter: edge node [test -> b], entrypoint [test], yetanalyzed [test -> a,  a -> d . d -> e] )
//     */
//
//
//    private void callGraphsAnalyzer(Edge e, SootMethod entryPoint, ArrayList<Edge> yetAnalyzed, Method test) {
//        //if the node isn't yet analyzed (for recursive function/method)
//        if (!yetAnalyzed.contains(e)) {
//            //retrieve a method from the node (the method at the end so i a node contain "a" that call "b", retrieve "b")
//            SootMethod m1 = e.getTgt().method();
//            //if the the method it'snt phantom
//            if (!m1.isPhantom()) {
//                //if the the method is method of one the applications classes
//                if (newProjectVersion.getProjectClasses().contains(m1.getDeclaringClass())) {
//                    //retrieve the call graph of the old project version
//                    CallGraph pCallGraph = previousProjectVersion.getCallGraph();
//                    //for all old node in the old project version's call graph
//                    for (Edge edge : pCallGraph) {
//                        //retrieve a method from the node (the method at the end so if a node contain "a" that call "b", retrieve "b")
//                        SootMethod m = edge.getTgt().method();
//                        //if the the method it'snt phantom
//                        if (!m.isPhantom()) {
//                            //if the the method is method of one the applications classes
//                            if (previousProjectVersion.getProjectClasses().contains(m.getDeclaringClass())) {
//                                //retrieve the test method that test this method
//                                //check that test is not null
//                                // assert test != null;
//                                //mark the method and it  test as pontetially new.
//                                addInMap(m1, test, newMethodsAndTheirTest);
//                                //if the methods have the same name and are in the same package
//                                if (haveSameNameAndAreInSamePackage(m, m1)) { //sono uguali: si --> cioè sno nello stesso package e hanno lo stesso nome? si
//
//                                    //if the methos is really a test methos (so no before, after,beforeclass and afterclass annotation, only test)
//                                    if (Util.isJunitTestCase(test)) {
//                                        //if the methods is a methods of a class that is not equal because the class constructor is different
//                                        if (differentObject.contains(m1.getDeclaringClass())) {
//                                            LOGGER.info("Added test:" +
//                                                    " " + entryPoint.getDeclaringClass() + "." + entryPoint.getName() + " that test method " + m.getDeclaringClass() + "." + m.getName() + " " +
//                                                    "because the constructor or fields of class " + m1.getDeclaringClass() + " is different from the previous version");
//                                            //add in a list this method and it test
//                                            addInMap(m1, test, methodsToRunForDifferenceInObject);
//                                            //unmark the method and it test as new.
//                                            removeToMap(m1, test, newMethodsAndTheirTest);
//
//
//                                            //stop, is indifferent is the method are the same in old project, because ia a method of a different object
//                                            break;
//                                        }
//                                        //else if the two methods have the same parameter
//                                        else if (haveSameParameter(m, m1)) { //hanno la stessa subsignature:
//                                            //if the two method are equal, so have the same body
//                                            if (!isEquals(m, m1)) {
//
//                                                //è già nella collezzione -> controllo da eliminare -> errore dovuto solo al binding del logger
//                                                // if (!isIn(test, differentMethodAndTheirTest).get()) {
//                                                LOGGER.info("Found change in this method:" +
//                                                        " " + m.getDeclaringClass() + "." + m.getName() + " "
//                                                        + "tested by: " + entryPoint.getDeclaringClass() + "." + entryPoint.getName());
//                                                //}
//                                                //add in a list with all different methods
//                                                addInMap(m1, test, differentMethodAndTheirTest);
//                                                //unmark the method and it test as new.
//                                                removeToMap(m1, test, newMethodsAndTheirTest);
//                                                //stop we have found it
//                                                // break;
//
//                                            }
//                                            //else
//                                            else {
//                                                //add in a list with all equals methods
//                                                addInMap(m1, test, equalsMethodAndTheirTest);
//                                                //unmark the method and it test as new.
//                                                removeToMap(m1, test, newMethodsAndTheirTest);
//                                                //stop we have found it
//                                                break;
//
//                                            }
//                                        }
//
//                                    } else {
//                                        //unmark the method and it test as new.
//                                        removeToMap(m1, test, newMethodsAndTheirTest);
//                                        //stop, the entry points its'nt a @Test Junit method.
//                                        break;
//
//
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        //add the node to the list of the yet analyzed node
//        yetAnalyzed.add(e);
//        //retrieve a method from the node (the method at the end so i a node contain a that call b, retrieve b)
//        SootMethod targetMethod = e.getTgt().method();
//        //get an iterator over the arches that going out from that method
//        Iterator<Edge> archesFromMethod = newProjectVersion.getCallGraph().edgesOutOf(targetMethod);
//        Edge edge;
//        //while the method are arches
//        while (archesFromMethod.hasNext()) {
//            //retieve the node
//            edge = archesFromMethod.next();
//            //if the node are not analyzed yet
//            if (!yetAnalyzed.contains(edge))
//                //recall this function with the new node, same entypoints and the list of the node analyzed yet.
//                callGraphsAnalyzer(edge, entryPoint, yetAnalyzed, test);
//
//        }
//    }
//
//    private void removeToMap(SootMethod m1, Method test, Set<Test> hashMap) {
//        Set<Test> holdHashMap = new HashSet<>(hashMap);
//
//        hashMap.forEach(test1 -> {
//
//            if (test1.getTestMethod().equals(test)) {
//                Method method = Util.findMethod(m1.getName(), m1.getDeclaringClass().getJavaStyleName(), m1.getDeclaringClass().getJavaPackageName(), newProjectVersion.getTarget());
//
//                test1.removeTestingMethod(method);
//
//                if (test1.getTestingMethods().isEmpty())
//                    holdHashMap.remove(test1);
//
//            }
//        });
//        hashMap.clear();
//        hashMap.addAll(holdHashMap);
//    }
//
//    private void addInMap(SootMethod m1, Method test, Set<Test> hashMap) {
//        AtomicBoolean is = isIn(test, hashMap);
//        Method method = Util.findMethod(m1.getName(), m1.getDeclaringClass().getJavaStyleName(), m1.getDeclaringClass().getJavaPackageName(), newProjectVersion.getTarget());
//        //java don't permit to retrieve the <clinit> and <init> method, but these are in callgraph, so when we try to retrieve this method
//        //java return an error. So we must check if the method are null.
//        if (method != null) {
//            if (!is.get()) {
//                HashSet<Method> ts = new HashSet<>();
//                ts.add(method);
//                hashMap.add(new Test(test, ts));
//            } else {
//
//                hashMap.forEach((Test test1) ->
//                {
//                    if (test1.getTestMethod().equals(test)) {
//                        test1.addTestingMethod(method);
//                    }
//
//                });
//            }
//        }
//
//    }
//
//    private AtomicBoolean isIn(Method test, Set<Test> hashMap) {
//        AtomicBoolean is = new AtomicBoolean(false);
//        hashMap.forEach((Test test1) ->
//        {
//            if (test1.getTestMethod().equals(test)) {
//                is.set(true);
//            }
//
//        });
//        return is;
//    }
//
//
//    private boolean isEquals(SootMethod m, SootMethod m1) {
//
//        try {
//            return m.getActiveBody().toString().equals(m1.getActiveBody().toString());
//        } catch (RuntimeException e) {
//            try {
//                return m.retrieveActiveBody().toString().equals(m1.retrieveActiveBody().toString());
//            } catch (RuntimeException e1) {
//                return true;
//            }
//        }
//    }
//
//    private boolean haveSameNameAndAreInSamePackage(SootMethod m, SootMethod m1) {
//        //getDeclaringClass
//
//
//        return m.getDeclaringClass().toString().equals(m1.getDeclaringClass().toString()) && m.getName().equals(m1.getName());
//    }
//
//
//    private boolean haveSameParameter(SootMethod m, SootMethod m1) {
//        return m.getSubSignature().equals(m1.getSubSignature());
//    }
//
//
//    private void findNewMethods() {
//        List<SootMethod> sootEntryPoints = newProjectVersion.getEntryPoints();
//        for (SootMethod sootTestMethod : sootEntryPoints) {
//            ArrayList<Edge> yetAnalyzed = new ArrayList<>();
//            Iterator<Edge> e = newProjectVersion.getCallGraph().edgesOutOf(sootTestMethod);
//            while (e.hasNext()) {
//                Edge hold = e.next();
//                if (Util.isJunitTestCase(sootTestMethod))
//                    analyzeCallGraphForNewMethod(hold, sootTestMethod, yetAnalyzed);
//            }
//        }
//
//    }
//
//
//    private void analyzeCallGraphForNewMethod(Edge e, SootMethod entryPoint, ArrayList<Edge> yetAnalyzed) {
//        SootMethod newMethod = e.getTgt().method();
//        if (!newMethod.isPhantom()) {
//            AtomicBoolean isPresent = new AtomicBoolean(false);
//            Collection<Set<Method>> equalsMethod = getEqualsMethods();
//            Collection<Set<Method>> differentMethod = getChangedMethods();
//            Collection<SootClass> differentsObject = new ArrayList<>(differentObject);
//            if (newProjectVersion.getProjectClasses().contains(newMethod.getDeclaringClass())) {
//
//                for (Set<Method> stringArrayList : equalsMethod) {
//                    stringArrayList.forEach(s -> {
//                        if (s.equals(newMethod.getName()))
//                            isPresent.set(true);
//                    });
//                }
//                //TODO: SE UN METODO è TESTATO DA PIù TEST, LI DEVO RESTITUIRE TUTTI? SECONDO ME SI
//              /*  for (Set<String> stringArrayList : getNewOrRemovedMethods()) {
//                    stringArrayList.forEach(s -> {
//                        if (s.equals(newMethod.getName()))
//                            isPresent.set(true);
//                    });
//                }
//*/
//                for (Set<Method> stringArrayList : differentMethod) {
//                    stringArrayList.forEach(s -> {
//                        if (s.equals(newMethod.getName()))
//                            isPresent.set(true);
//                    });
//                }
//
//                for (SootClass sootClass : differentsObject) {
//                    if (newMethod.getDeclaringClass().equals(sootClass))
//                        isPresent.set(true);
//                }
//
//                if (!isPresent.get()) {
//                    Method test = Util.findMethod(entryPoint.getName(), entryPoint.getDeclaringClass().getJavaStyleName(), entryPoint.getDeclaringClass().getJavaPackageName(), newProjectVersion.getTarget());
//                    LOGGER.info("Found new  method:" +
//                            " " + newMethod.getDeclaringClass() + "." + newMethod.getName() + " "
//                            + "tested by: " + entryPoint.getDeclaringClass() + "." + entryPoint.getName());
//                    addInMap(newMethod, test, newMethodsAndTheirTest);
//                }
//
//
//            }
//
//
//            yetAnalyzed.add(e);
//            Iterator<Edge> bho1 = newProjectVersion.getCallGraph().edgesOutOf(newMethod);
//            Edge e3;
//
//
//            while (bho1.hasNext()) {
//                e3 = bho1.next();
//                if (!yetAnalyzed.contains(e3))
//                    analyzeCallGraphForNewMethod(e3, entryPoint, yetAnalyzed);
//            }
//        }
//    }
//
//}
//
//
//
//
