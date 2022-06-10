package CATTO.test.selector;

import CATTO.test.Test;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import soot.Modifier;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.Edge;
import CATTO.project.NewProject;
import CATTO.util.JunitUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestSelector {

    private final ConcurrentHashMap<SootMethod, HashSet<String>> differentMethodAndTheirTest;
    private final ConcurrentHashMap<SootMethod, HashSet<String>> newMethodsAndTheirTest;
    private final ConcurrentHashMap<SootMethod, HashSet<String>> methodsToRunForDifferenceInObject;
    private final ConcurrentHashMap<SootMethod, HashSet<String>> methodsToRunForSetUpOrTearDown;
    private final ConcurrentHashMap<SootMethod, HashSet<String>> methodsToRunForInit;


    private final NewProject newProjectVersion;
    private static final Logger LOGGER = Logger.getLogger(TestSelector.class);


    private final HashSet<SootMethod> differentMethods;
    private final HashSet<SootMethod> newMethods;
    private final HashSet<Test> differentTest;
    private final HashSet<SootClass> differentObject;




    /**
     * @param newProjectVersion the new project version
     * @param differentMethods
     * @param differentTest
     * @param newMethods
     * @param differentObject
     */
    public TestSelector(NewProject newProjectVersion, HashSet<SootMethod> differentMethods, HashSet<Test> differentTest, HashSet<SootMethod> newMethods, HashSet<SootClass> differentObject) {
        this.differentObject = differentObject;
        this.methodsToRunForDifferenceInObject = new ConcurrentHashMap<>();
        this.methodsToRunForInit = new ConcurrentHashMap<>();
        this.methodsToRunForSetUpOrTearDown = new ConcurrentHashMap<>();
        this.differentMethodAndTheirTest = new ConcurrentHashMap<>();
        this.newMethodsAndTheirTest = new ConcurrentHashMap<>();
        this.differentMethods = differentMethods;
        this.newProjectVersion = newProjectVersion;
        this.differentTest = differentTest;
        this.newMethods = newMethods;
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
    private Set<Test> getMethodsToRunForSetUpOrTearDownOrInit(ConcurrentHashMap<SootMethod, HashSet<String>> inToSearch) {
        HashSet<Test> hst = new HashSet<>();
        for (Map.Entry<SootMethod, HashSet<String>> en : inToSearch.entrySet()) {
            en.getKey().getDeclaringClass().getMethods().forEach(sootMethod -> {
                if (JunitUtil.isJunitTestCase(sootMethod)) {
                    Test t = new Test(sootMethod, en.getValue());
                    hst.add(t);
                }
            });
        }
        return hst;

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
        allTest.addAll(getMethodsToRunForSetUpOrTearDownOrInit(methodsToRunForInit));
        allTest.addAll(getMethodsToRunForSetUpOrTearDownOrInit(methodsToRunForSetUpOrTearDown));

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

/*

        findDifferenceInHierarchy();


        findDifferentMethods();
        findNewMethods();
        LOGGER.info("comparing the two test suite to see if there are differents tests");
        comparingTest();
        LOGGER.info("comparing the two classes to see if the constructors are equals");
        isTheSameObject();
*/

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

    private void addInMap(SootMethod m1, SootMethod test, ConcurrentHashMap<SootMethod, HashSet<String>> hashMap) {
        if (hashMap.containsKey(test)) {
            hashMap.get(test).add(m1.getDeclaringClass().getName() + "." + m1.getName());
        } else {
            HashSet<String> add = new HashSet<>();
            add.add(m1.getDeclaringClass().getName() + "." + m1.getName());
            hashMap.put(test, add);
        }
    }


    private void first(Set<SootMethod> hashset, ConcurrentHashMap mapInToAdd, ExecutorService executorService) {
        for (SootMethod method : hashset) {
            Analyzer an = new Analyzer(method, mapInToAdd);
            executorService.execute(an);
        }

    }

    private void run1(Edge e, SootMethod m, Set<SootMethod> yetAnalyzed, ConcurrentHashMap mapInToAdd) {
//        if (Util.isJunitTestCase(e.src()) &&  (Modifier.isAbstract(e.src().method().getDeclaringClass().getModifiers()) || Modifier.isInterface(e.src().method().getDeclaringClass().getModifiers())))
//            LOGGER.info("YES");

        //  allMethodsAnalyzed.add(e.src());
            /*TODO: Spostare il conotrollo sulla classe astratta/interfaccia da un altra parte
             Quello che succede è  che nel metodo CreateEntryPoints in NewProject non vengono presi, correttamente, i metodi delle classi
             astratta/interfacce come metodi di test, quindi questi non compaiono come entry points nel grafo.
             Ma salendo dal basso questo algoritmo se trova un metodo che rispecchia i cirteri per essere un metodo di test, viene selezioanto. Non possiamo aggiungere dirattemente questo controllo nel metodo utilizato per controllare se è un metodo di test, perchè anche se in una classe astratta un metodo può essere di test, venendo ereditato da un altra classe. Probabilemente sarà necessario creare un metodo in Uitl per i metodi di test ereditati, in cui non eseguire il controllo sulla classe astratta/interfaccia ed uno in cui controllare se il metodo di test fa parte di una classe astratta o meno. */
        //In alcuni casi abbiamo dei test di classi di test concrete che chiamano test concreti in classi astratte. Questo rende necessatrio, in qualunque caso il controllo sulla classe astratte in questo punto dell'algoritmo.

        if (yetAnalyzed.contains(e.src()))
            return;

        //per non analizzare due volte lo stesso metodo partendo da test differenti (?)
        if (differentMethods.contains(e.src())) {
            return;
        }
        if (!Modifier.isAbstract(e.src().method().getDeclaringClass().getModifiers())) {

            if (JunitUtil.isJunitTestCase(e.src()))
                addInMap(m, e.src(), mapInToAdd);
                //se un setUp o un tearDown o un costruttore testa un metodo modificato
            else if (JunitUtil.isSetup(e.src()) || JunitUtil.isTearDown(e.src()) || ( (e.src().getName().equals("<init>") || e.src().getName().equals("<clinit>")) && JunitUtil.isATestClass(e.src())))

                addInMap(m, e.src(), methodsToRunForSetUpOrTearDown);

        }

        yetAnalyzed.add(e.src());


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
            HashSet<SootMethod> yetAnalyzed = new HashSet<>();
            while (iterator.hasNext()) {
                Edge e = iterator.next();

                run1(e, sootMethodM1, yetAnalyzed, mapInToAdd);

            }

        }


    }

}








