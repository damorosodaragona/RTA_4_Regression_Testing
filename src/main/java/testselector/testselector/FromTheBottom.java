package testselector.testselector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import soot.Modifier;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.Edge;
import testselector.project.NewProject;
import testselector.project.PreviousProject;
import testselector.project.Project;
import testselector.project.SootMethodMoved;
import testselector.util.Util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FromTheBottom {

    private final ConcurrentHashMap<SootMethod, HashSet<String>> differentMethodAndTheirTest;
    private final ConcurrentHashMap<SootMethod, HashSet<String>> newMethodsAndTheirTest;
    private final Set<SootClass> differentObject;
    private final ConcurrentHashMap<SootMethod, HashSet<String>> methodsToRunForDifferenceInObject;
    private final ConcurrentHashMap<SootMethod, HashSet<String>> methodsToRunForSetUp;


    private final Set<Test> differentTest;
    private final Set<SootMethod> allMethodsAnalyzed;
    private final PreviousProject previousProjectVersion;
    private final NewProject newProjectVersion;
    private static final Logger LOGGER = Logger.getLogger(FromTheBottom.class);
    private HashSet<SootMethod> differentMethods;
    private HashSet<SootMethod> newMethods;

    private HashSet<SootMethod> equalsMethods;


    /**
     * @param previousProjectVersion the old project version
     * @param newProjectVersion      the new project version
     */
    public FromTheBottom(Project previousProjectVersion, Project newProjectVersion) {
        this.methodsToRunForDifferenceInObject = new ConcurrentHashMap<>();
        this.differentObject = new HashSet<>();
        this.methodsToRunForSetUp = new ConcurrentHashMap<>();
        this.differentMethodAndTheirTest = new ConcurrentHashMap<>();
        this.newMethodsAndTheirTest = new ConcurrentHashMap<>();
        this.differentTest = new HashSet<>();
        this.previousProjectVersion = (PreviousProject) previousProjectVersion;
        this.newProjectVersion = (NewProject) newProjectVersion;
        this.differentMethods = new HashSet<>();
        this.allMethodsAnalyzed = new HashSet<>();
        this.equalsMethods = new HashSet<>();
        this.newMethods = new HashSet<>();
        LOGGER.setLevel(Level.INFO);
    }


    /**
     * Get a set with test that test methods different from the old version project
     *
     * @return a set with test that test method different from the old version project
     */
    public Set<Test> getDifferentMethodAndTheirTest() {
        HashSet<Test> hst = new HashSet<>();
        Iterator<Map.Entry<SootMethod, HashSet<String>>> it = differentMethodAndTheirTest.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<SootMethod, HashSet<String>> en = it.next();
            Test t = new Test(en.getKey(), en.getValue());
            hst.add(t);
        }
        return hst;
    }

    /**
     * Get a set with tests that test new methods, so the methods that aren't in the old project version
     *
     * @return a set with tests that test new methods
     */
    public Set<Test> getNewMethodsAndTheirTest() {
        HashSet<Test> hst = new HashSet<>();
        Iterator<Map.Entry<SootMethod, HashSet<String>>> it = newMethodsAndTheirTest.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<SootMethod, HashSet<String>> en = it.next();
            Test t = new Test(en.getKey(), en.getValue());
            hst.add(t);
        }
        return hst;

    }

    /**
     * Get a set with tests that test new methods, so the methods that aren't in the old project version
     *
     * @return a set with tests that test new methods
     */
    private Set<Test> getMethodsToRunForDifferenceInObject() {
        HashSet<Test> hst = new HashSet<>();
        Iterator<Map.Entry<SootMethod, HashSet<String>>> it = methodsToRunForDifferenceInObject.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<SootMethod, HashSet<String>> en = it.next();
            Test t = new Test(en.getKey(), en.getValue());
            hst.add(t);
        }
        return hst;

    }

    /**
     * Get a set with tests that test new methods, so the methods that aren't in the old project version
     *
     * @return a set with tests that test new methods
     */
    private Set<Test> getMethodsToRunForSetUp() {
        HashSet<Test> hst = new HashSet<>();
        Iterator<Map.Entry<SootMethod, HashSet<String>>> it = methodsToRunForSetUp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<SootMethod, HashSet<String>> en = it.next();
                newProjectVersion.getMoved().forEach(sootMethodMoved -> {
                    if(sootMethodMoved.getMethodsMoved().contains(en.getKey()))
                        sootMethodMoved.getMethodsMoved().forEach(sootMethod -> {
                            if(Util.isJunitTestCase(sootMethod)){
                                Test t = new Test(sootMethod, en.getValue());
                                hst.add(t);
                            }
                        });
                    else
                       en.getKey().getDeclaringClass().getMethods().forEach(sootMethod -> {
                            if(Util.isJunitTestCase(sootMethod)){
                                Test t1 = new Test(sootMethod, en.getValue());
                                hst.add(t1);
                            }
                        });
                });

        }
        return hst;

    }


    /**
     * Get a string collection with the name of the methods that are dfferent from the old project version
     *
     * @return a collection with the java style name (package.classname) of the methods that are different from the old project version
     */
    public Collection<String> getChangedMethods() {
        Collection<String> changedMethodsCopy = new ArrayList<>();
        differentMethods.forEach(changedMethod -> changedMethodsCopy.add(changedMethod.getDeclaringClass().getName() + "." + changedMethod.getName()));
        return changedMethodsCopy;
    }

    /**
     * Get a string collection with the name of the methods that are new, so that aren't in the old project version
     *
     * @return a collection with the java style name (package.classname) of the methods that are new
     */
    public Collection<String> getNewMethods() {

        Collection<String> newMethodsCopy = new ArrayList<>();
        newMethods.forEach(newMethod -> newMethodsCopy.add(newMethod.getDeclaringClass().getName() + "." + newMethod.getName()));
        return newMethodsCopy;
    }
    /**
     * Get a string collection with the name of the methods that are dfferent from the old project version and that are covered by some tests
     *
     * @return a collection with the java style name (package.classname) of the methods that are different from the old project version
     */

    /*public Collection<Set<String>> getCoveredChangedMethods() {
        Collection<Set<String>> changedMethods = new ArrayList<>();
        differentMethodAndTheirTest.forEach(changedMethod -> changedMethods.add(changedMethod.getTestingMethods()));
        return changedMethods;
    }*/

    /**
     * Get a string collection with the name of the methods that are new, so that aren't in the old project version and that are covered by some tests
     *
     * @return a collection with the java style name (package.classname) of the methods that are new
     */
  /*  public Collection<Set<String>> getCoveredNewMethods() {

        Collection<Set<String>> newMethods = new ArrayList<>();
        newMethodsAndTheirTest.forEach(newMethod -> newMethods.add(newMethod.getTestingMethods()));
        return newMethods;
    }
*/

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
        allTest.addAll(getMethodsToRunForDifferenceInObject());
        allTest.addAll(differentTest);
        allTest.addAll(getMethodsToRunForSetUp());
        return allTest;
    }


    /**
     * Get all test that are necessary to run for the new project version.
     *
     * @return a set of Test with all test that are necessary to run for the new project version.
     */
    public Set<Test> selectTest() {

        //PackManager.v().runPacks();

        // newProjectVersion.createCallgraph();
        // previousProjectVersion.moveToAnotherPackage(newProjectVersion.getMovedToAnotherPackage());


        findDifferenceInHierarchy();


        findDifferentMethods();
        findNewMethods();
        LOGGER.info("comparing the two test suite to see if there are differents tests");
        comparingTest();
        LOGGER.info("comparing the two classes to see if the constructors are equals");
        isTheSameObject();

        ExecutorService executorService = Executors.newCachedThreadPool();
        first(differentMethods, differentMethodAndTheirTest, executorService);
        first(newMethods, newMethodsAndTheirTest, executorService);
        for (SootClass s : differentObject) {
            differentMethods.addAll(s.getMethods());
            first(new HashSet<>(s.getMethods()), methodsToRunForDifferenceInObject, executorService);
        }
        executorService.shutdown();

        while (!executorService.isTerminated()) {
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
        HashSet<SootClass> p1Class = (HashSet<SootClass>) newProjectVersion.getProjectClasses();
        HashSet<SootClass> copyPClass = (HashSet<SootClass>) previousProjectVersion.getProjectClasses();
        for (SootClass s1 : p1Class) {
            SootClass classToRemove;
            List<SootClass> pClass = new ArrayList<>(copyPClass);
            for (SootClass s : pClass) {
                if (s.getName().equals(s1.getName())) {
                    classToRemove = s;
                    List<SootMethod> ms1 = s1.getMethods();
                    for (SootMethod m1 : ms1) {
                        boolean isMoved = false;
                        if (Modifier.isAbstract(m1.getModifiers())) {
                            equalsMethods.add(m1);
                            continue;
                        }
//                        // mi assicuro che il metodo che sto confrontando non sia il metodo della classe madre ma quello della classe figlia
//                        for(SootMethodMoved moved : newProjectVersion.getMoved()){
//                            if(moved.isMoved(m1)) {
//                                isMoved = true;
//                                break;
//                            }
//                        }
//
//                        if(isMoved)
//                            continue;


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
     * Compare every test in the two versions of the project.
     * If there is a test method with the same name, in the same class and in the same package in the
     * both versions of the project this method is compared and if it's not equals is selected regardless
     * of the methods it tests.
     */
    private void comparingTest() {

        HashSet<SootMethod> toDelete = new HashSet<>();
        for (SootMethod testMethod : differentMethods) {

            if (Util.isATestMethod(testMethod)) {
                if (Modifier.isAbstract(testMethod.getDeclaringClass().getModifiers())) {
                    toDelete.add(testMethod);
                    continue;
                }
                if (Util.isSetup(testMethod)) {
                    for (SootMethod s : testMethod.getDeclaringClass().getMethods()) {
                        if (Util.isJunitTestCase(s)) {

                            /*boolean isIn = false;
                            for (Test t : differentTest) {
                                if (t.getTestMethod().equals(s))
                                    isIn = true;
                            }

                            if (!isIn) {
                                LOGGER.info("The test: " + s.getDeclaringClass().getName() + "." + s.getName() + " has been added because the setUp of it's class has been changed");
                                differentTest.add(new Test(s));
                            }*/

                            differentTest.add(new Test(s));
                        }
                    }

                } else {
                    //aggiungo ai test differenti solo i test -> metodi con @Test. I @Before,@After ecc ecc verrano eseguiti lo stesso
                    if (Util.isJunitTestCase(testMethod)) {
                        LOGGER.info("The test: " + testMethod.getDeclaringClass().getName() + "." + testMethod.getName() + " has been added because it is in both versions of the project but has been changed");
                        differentTest.add(new Test(testMethod));
                    }

                }
            }
        }

        differentTest.forEach(test -> differentMethods.remove(test.getTestMethod()));
        toDelete.forEach(test -> differentMethods.remove(test));

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


    private void addInMap(SootMethod m1, SootMethod test, ConcurrentHashMap<SootMethod, HashSet<String>> hashMap) {
        if (hashMap.containsKey(test)) {
            hashMap.get(test).add(m1.getDeclaringClass().getName() + "." + m1.getName());
        } else {
            HashSet<String> add = new HashSet<>();
            add.add(m1.getDeclaringClass().getName() + "." + m1.getName());
            hashMap.put(test, add);
        }
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

    private void first(Set<SootMethod> hashset, ConcurrentHashMap mapInToAdd, ExecutorService executorService) {
        for (SootMethod method : hashset) {
            Analyzer an = new Analyzer(method, mapInToAdd);
            executorService.execute(an);
        }

    }

    private void run1(Edge e, SootMethod m, List<Edge> yetAnalyzed, ConcurrentHashMap mapInToAdd) {
//        if (Util.isJunitTestCase(e.src()) &&  (Modifier.isAbstract(e.src().method().getDeclaringClass().getModifiers()) || Modifier.isInterface(e.src().method().getDeclaringClass().getModifiers())))
//            LOGGER.info("YES");

        allMethodsAnalyzed.add(e.src());
            /*TODO: Spostare il conotrollo sulla classe astratta/interfaccia da un altra parte
             Quello che succede è  che nel metodo CreateEntryPoints in NewProject non vengono presi, correttamente, i metodi delle classi
             astratta/interfacce come metodi di test, quindi questi non compaiono come entry points nel grafo.
             Ma salendo dal basso questo algoritmo se trova un metodo che rispecchia i cirteri per essere un metodo di test, viene selezioanto. Non possiamo aggiungere dirattemente questo controllo nel metodo utilizato per controllare se è un metodo di test, perchè anche se in una classe astratta un metodo può essere di test, venendo ereditato da un altra classe. Probabilemente sarà necessario creare un metodo in Uitl per i metodi di test ereditati, in cui non eseguire il controllo sulla classe astratta/interfaccia ed uno in cui controllare se il metodo di test fa parte di una classe astratta o meno. */

//        if (!newProjectVersion.getEntryPoints().contains(e.src()) && !Modifier.isAbstract(e.src().method().getDeclaringClass().getModifiers()) && !Modifier.isInterface(e.src().method().getDeclaringClass().getModifiers())) {
            if (Util.isJunitTestCase(e.src())) {
                addInMap(m, e.src(), mapInToAdd);
                return;
            }

            if (Util.isSetup(e.src()) || (e.src().getName().equals("<init>") && Util.isATestClass(e.src()))) {

                addInMap(m, e.src(), methodsToRunForSetUp);

                return;

            }

//        }
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
        private SootMethod sootMethodM1;
        private final ConcurrentHashMap mapInToAdd;

        public Analyzer(SootMethod hashset, ConcurrentHashMap mapInToAdd) {
            this.sootMethodM1 = hashset;
            this.mapInToAdd = mapInToAdd;
        }


        @Override
        public void run() {


            Iterator<Edge> iterator = newProjectVersion.getCallGraph().edgesInto(sootMethodM1);
            ArrayList<Edge> yetAnalyzed = new ArrayList<>();
            while (iterator.hasNext()) {
                Edge e = iterator.next();

                run1(e, sootMethodM1, yetAnalyzed, mapInToAdd);

            }

        }


    }

}








