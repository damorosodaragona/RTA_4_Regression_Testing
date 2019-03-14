package testselector.testSelector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.toolkits.callgraph.Edge;
import testselector.main.Main;
import testselector.project.Project;
import testselector.util.ClassPathUpdater;
import testselector.util.Util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class OnlyOneGraph {

    private final boolean alsoNew;
    private final Set<Test> differentMethodAndTheirTest;
    private final Set<Test> equalsMethodAndTheirTest;
    private final Set<Test> newMethodsAndTheirTest;
    private final Set<SootClass> differentObject;
    private final Set<Test> methodsToRunForDifferenceInObject;
    private final Set<Test> differentTest;

    private final Project previousProjectVersion;
    private final Project newProjectVersion;
    private static final Logger LOGGER = Logger.getLogger(Main.class);
    private ArrayList<Edge> differenteEdge;
    private HashSet<SootMethod> differentMethods;


    /**
     * @param previousProjectVersion the old project version
     * @param newProjectVersion      the new project version
     * @param alsoNew
     */
    public OnlyOneGraph(Project previousProjectVersion, Project newProjectVersion, boolean alsoNew) {
        this.methodsToRunForDifferenceInObject = new HashSet<>();
        this.differentObject = new HashSet<>();
        this.differentMethodAndTheirTest = new HashSet<>();
        this.equalsMethodAndTheirTest = new HashSet<>();
        this.newMethodsAndTheirTest = new HashSet<>();
        this.differentTest = new HashSet<>();
        this.previousProjectVersion = previousProjectVersion;
        this.newProjectVersion = newProjectVersion;
        this.alsoNew = alsoNew;
        this.differenteEdge = new ArrayList<>();
        this.differentMethods = new HashSet<>();
        try {
            ClassPathUpdater.add(this.newProjectVersion.getClassPath());
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
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
    //TODO: SNELLIRE IL CODICE? QUI CREO UN ARRAYLIST DI ARRAYLIST. POTREI CREARE UN ARRYLIST IN MODO DA RENDERE PIù USABILE IL CODICE.
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

    /*
     * Get all test that are necessary to run for the new project version.
     * If the option -new is enable this test return also the test that test the new methods in the new version of the project,
     * else for default return only the test that test the method that are different in the two version of the projcet.
     * If there is an object that have some difference in the constructor this method return all test that test the method of that class.
     * @return a set of Test with all test that are necessary to run for the new project version.
     */
    public Set<Test> getAllTestToRun() {
        Set<Test> allTest = new HashSet<>();
        allTest.addAll(getDifferentMethodAndTheirTest());
        allTest.addAll(getNewMethodsAndTheirTest());
        allTest.addAll(methodsToRunForDifferenceInObject);
        allTest.addAll(differentTest);
        return allTest;
    }


    /**
     * Get all test that are necessary to run for the new project version.
     * If the option -new is enable this test return also the test that test the new methods in the new version of the project,
     * else for default return only the test that test the method that are different in the two version of the projcet.
     * If there is an object that have some difference in the constructor this method return all test that test the method of that class.
     *
     * @return a set of Test with all test that are necessary to run for the new project version.
     */
    public Set<Test> selectTest() {

        findDifferentMethods();

        LOGGER.info("comparing the two test suite to see if there are differents tests");
        comparingTest();
        LOGGER.info("comparing the two classes to see if the constructors are equals");
        isTheSameObject();
        LOGGER.info("starting comparing callgraph");
        int count = 0;
        for (SootMethod sootMethodM1 : Scene.v().getEntryPoints()) {
            Analyzer a = new Analyzer(sootMethodM1);
            try {
                a.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }




        LOGGER.debug("Analyzed Test: " + count);
        newMethodsAndTheirTest.forEach(test -> test.getTestingMethods().forEach(s -> {
                            LOGGER.info("Found new method: " + s
                                    + " tested by: " + test.getTestMethod().getDeclaringClass().getName() + "." + test.getTestMethod().getName());
                        }
                ));
                //if (alsoNew)
                //  findNewMethods();
        return getAllTestToRun();
    }





    /**
     * Get all test that are necessary to run for the new project version.
     * If the option -new is enable this test return also the test that test the new methods in the new version of the project,
     * else for default return only the test that test the method that are different in the two version of the projcet.
     * If there is an object that have some difference in the constructor this method return all test that test the method of that class.
     *
     * @return a set of Test with all test that are necessary to run for the new project version.
     */
/*    public Set<Test> selectTest() {

        findDifferentMethods();

        LOGGER.info("comparing the two test suite to see if there are differents tests");
        comparingTest();
        LOGGER.info("comparing the two classes to see if the constructors are equals");
        isTheSameObject();

        LOGGER.info("starting comparing callgraph");

        for (SootMethod sootMethodM1 : newProjectVersion.getEntryPoints()) {
            ArrayList<Edge> yetAnalyzed = new ArrayList<>();
            Iterator<Edge> iteratorP1 = newProjectVersion.getCallGraph().edgesOutOf(sootMethodM1);
                boolean continueThisSubCallgraph = true;

                for (Test t : differentTest) {
                    if (t.getTestMethod().equals(sootMethodM1))
                        continueThisSubCallgraph = false;
                }
                if (Util.isJunitTestCase(sootMethodM1, newProjectVersion.getJunitVersion()) && continueThisSubCallgraph) {
                    LOGGER.info("Analyzing: " + sootMethodM1.getDeclaringClass() + "." + sootMethodM1.getName());
                    while (iteratorP1.hasNext() && continueThisSubCallgraph) {
                        Edge e1 = iteratorP1.next();
                        //if (newProjectVersion.getProjectClasses().contains(e1.getTgt().method().getDeclaringClass())) {
                        continueThisSubCallgraph = callGraphsAnalyzer(e1, yetAnalyzed, sootMethodM1);
                        //}
                    }
                }


        }


        newMethodsAndTheirTest.forEach(test -> test.getTestingMethods().forEach(s -> {
                    LOGGER.info("Found new method: " + s
                            + " tested by: " + test.getTestMethod().getDeclaringClass().getName() + "." + test.getTestMethod().getName());
                }
        ));
        //if (alsoNew)
        //  findNewMethods();
        return getAllTestToRun();

    }*/
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
                    //            LOGGER.debug("comparing: " + s.getName() + " -> " + s1.getName() );
                    classToRemove = s;
                    List<SootMethod> ms1 = s1.getMethods();
                    //SootMethod methodToRemove;
                    for (SootMethod m1 : ms1) {
                        if (Modifier.isAbstract(m1.getModifiers()))
                            continue;
                        for (SootMethod m : s.getMethods()) {
                            if (haveSameParameter(m, m1) && m.getName().equals(m1.getName())) {
                                //       methodToRemove = m;
                                if (!isEquals(m, m1)) {

                                    differentMethods.add(m1);
                                }
                                //l'oggetto SootClass non è clonable, quindi ha un riferimento strong con l'oggetto, rimuovendo il metodo da s, lo rimuovo anche dall'oggetto vero e proprio
                                //                     s.removeMethod(methodToRemove);
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
        //TODO: testare differenze in setUp
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
        //4. elimianre questa linea
        //    HashSet<SootMethod> hold = new HashSet<>();
        differentMethods.forEach(sootMethod -> {
            if (sootMethod.getName().startsWith("<clinit"))
                //Todo:  per ripristinare il controllo degli ini e clinit:
                // 1. decommentare la linea di codice seguente
                differentObject.add(sootMethod.getDeclaringClass());
            //2. eliminare questa linea
            // hold.add(sootMethod);
        });
        //3. eliminare questa linea
        //  differentMethods.removeAll(hold);

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

        //if the node isn't yet analyzed (for recursive function/method)
        //  if (!yetAnalyzed.contains(e1)) {
        //retrieve a method from the node (the method at the end so i a node contain "a" that call "b", retrieve "b")
        //1 cercare e in tutto p

        SootMethod srcM1 = e1.src();
        SootMethod tgtM1 = e1.tgt();


        boolean continueThisSubCallGraph = true;

        if (!isDifferentObject(entryPoint, srcM1) && !isDifferentObject(entryPoint, tgtM1)) {

            if (!yetAnalyzed.contains(e1)) {
                //se nessuno dei due
                //if (src || tgt) {
                //Probabilmente controllo da eliminare
                differenteEdge.remove(e1);

                if (newProjectVersion.getProjectClasses().contains(srcM1.getDeclaringClass())) {


                    //if (!isDifferentObject(entryPoint, test, srcM1)) {
                    //controllo che il metodo diverso ed il suo test non siano giù nella collezzione.
                    if (differentMethods.contains(srcM1)) {
                        synchronized (LOGGER) {
                            LOGGER.info("Found change in this method:" +
                                    " " + srcM1.getDeclaringClass() + "." + srcM1.getName() + " "
                                    + "tested by: " + entryPoint.getDeclaringClass() + "." + entryPoint.getName());
                        }
                        //}
                        //Todo:Eliminare questa riga di codice se si vogliono trovare tutti i metodo differenti che il test testa
                        continueThisSubCallGraph = false;
                        //add in a list with all different methods
                        synchronized (differentMethodAndTheirTest) {
                            addInMap(srcM1, entryPoint, differentMethodAndTheirTest);
                        }
                        //unmark the method and it test as new.
                        //          removeToMap(srcM1, test, newMethodsAndTheirTest);
                        //stop we have found it
                        // break;

                    }


                    // }
                }
                if (continueThisSubCallGraph) {
                    if (newProjectVersion.getProjectClasses().contains(tgtM1.getDeclaringClass())) {
                        if (differentMethods.contains(tgtM1)) {
                            //if (!isDifferentObject(entryPoint, test, tgtM1)) {
                            synchronized (LOGGER) {
                                LOGGER.info("Found change in this method:" +
                                        " " + tgtM1.getDeclaringClass() + "." + tgtM1.getName() + " "
                                        + "tested by: " + entryPoint.getDeclaringClass() + "." + entryPoint.getName());
                            }
                            //}
                            //add in a list with all different methods
                            synchronized (differentMethodAndTheirTest) {
                                addInMap(tgtM1, entryPoint, differentMethodAndTheirTest);
                            }
                            //Todo:Eliminare questa riga di codice se si vogliono trovare tutti i metodo differenti che il test testa
                            continueThisSubCallGraph = false;
                            //unmark the method and it test as new.
                            //     removeToMap(srcM1, test, newMethodsAndTheirTest);
                            //stop we have found it
                            // break;


                        }
                    }
                    //}
                }
                yetAnalyzed.add(e1);


                //retrieve a method from the node (the method at the end so i a node contain a that call b, retrieve b)
                SootMethod targetM1Method = e1.getTgt().method();
                //if (newProjectVersion.getProjectClasses().contains(targetM1Method.getDeclaringClass())) {
                //get an iterator over the arches that going out from that method
                Iterator<Edge> archesFromTargetM1Method = newProjectVersion.getCallGraph().edgesOutOf(targetM1Method);

                Edge edgeP1;
                //retrieve a method from the node (the method at the end so i a node contain a that call b, retrieve b)
                //get an iterator over the arches that going out from that method
                //while the method are arches
                while (archesFromTargetM1Method.hasNext() && continueThisSubCallGraph) {
                    edgeP1 = archesFromTargetM1Method.next();
                    // if (newProjectVersion.getProjectClasses().contains(edgeP1.getTgt().method().getDeclaringClass())) {
                    //retieve the node
                    //if the node are not analyzed yet
                    //recall this function with the new node, same entypoints and the list of the node analyzed yet.
                    continueThisSubCallGraph = callGraphsAnalyzer(edgeP1, yetAnalyzed, entryPoint);

                    //}
                }
                // }
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

    private void removeToMap(SootMethod m1, SootMethod test, Set<Test> hashMap) {
        Set<Test> holdHashMap = new HashSet<>(hashMap);

        hashMap.forEach(test1 -> {

            if (test1.getTestMethod().equals(test)) {
                //  Method method = Util.findMethod(m1.getName(), m1.getDeclaringClass().getJavaStyleName(), m1.getDeclaringClass().getJavaPackageName(), newProjectVersion.getTarget(), newProjectVersion.getClassPath());

                test1.removeTestingMethod(m1.getDeclaringClass().toString() + "." + m1.getName());

                if (test1.getTestingMethods().isEmpty())
                    holdHashMap.remove(test1);

            }
        });
        hashMap.clear();
        hashMap.addAll(holdHashMap);
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


        //return Grimp.v().newBody(m).toString().equals(Grimp.v().newBody(m1).toString());
        // return Shimple.v().newBody(m).toString().equals(Shimple.v().newBody(m1).toString());
        String s = null;
        String s1 = null;
        try {
            /*if (!(m.retrieveActiveBody() instanceof BafBody))
                m.setActiveBody(Baf.v().newBody(m));

            if (!(m1.retrieveActiveBody() instanceof BafBody))
                m1.setActiveBody(Baf.v().newBody(m1));*/
            //     s = m.getActiveBody().toString();
            //     s1 = m1.getActiveBody().toString();
            //    LOGGER.debug(s);
            //     LOGGER.debug(s1);
            Iterator<Unit> itu = m.getActiveBody().getUnits().iterator();
            Iterator<Unit> itu1 = m1.getActiveBody().getUnits().iterator();
            while (itu.hasNext()) {
                s = itu.next().toString();
                s1 = itu1.next().toString();
                if (!s.equals(s1))
                    if (s.contains("@") && s1.contains("@")) {
                        if (!s.split("@")[1].equals(s1.split("@")[1]))
                            return false;
                    } else
                        return false;
            }

            return true;
            //   return m.getActiveBody().toString().equals(m1.getActiveBody().toString());

        } catch (RuntimeException e) {
            try {
                //      LOGGER.debug(s);
                //      LOGGER.debug(s1, e);
                try {
                    m.retrieveActiveBody();
                } catch (RuntimeException em) {
                    return true;
                }
                try {
                    m1.retrieveActiveBody();
                } catch (RuntimeException em1) {
                    return true;
                }
                Iterator<Unit> itu = m.getActiveBody().getUnits().iterator();
                Iterator<Unit> itu1 = m1.getActiveBody().getUnits().iterator();
                while (itu.hasNext()) {
                    if (itu.next().toString().equals(itu1.next().toString()))
                        return false;

                }

                return true;
            } catch (RuntimeException e1) {
                //        LOGGER.debug(s1, e1);

                return true;
            }
        }


    }

    private boolean haveSameNameAndAreInSamePackage(SootMethod m, SootMethod m1) {
        //getDeclaringClass


        return m.getDeclaringClass().toString().equals(m1.getDeclaringClass().toString()) && m.getName().equals(m1.getName());
    }


    private boolean haveSameParameter(SootMethod m, SootMethod m1) {
        return m.getSubSignature().equals(m1.getSubSignature());
    }


    private void findNewMethods() {
        //TODO: da implementare
    }


    private class  Analyzer extends Thread{
        private int count;
        private SootMethod sootMethodM1;
        private Thread thread;

        public Analyzer(SootMethod sootMethodM1) {
            this.sootMethodM1 = sootMethodM1;
            this.start();
        }
        @Override
        public void start(){
        if(thread == null) {
            thread = new Thread(this, sootMethodM1.getDeclaringClass().getJavaStyleName() + "." + sootMethodM1.getName());
            thread.start();
        }
        }

        @Override
        public void run() {
            Iterator<Edge> iterator = newProjectVersion.getCallGraph().edgesOutOf(sootMethodM1);
            while (iterator.hasNext()) {
                Edge e = iterator.next();
                boolean continueThisSubCallgraph = true;

                for (Test t : differentTest) {
                    if (t.getTestMethod().equals(e.tgt()))
                        continueThisSubCallgraph = false;
                }
                if (Util.isJunitTestCase(e.tgt(), newProjectVersion.getJunitVersion()) && continueThisSubCallgraph) {
                    ArrayList<Edge> yetAnalyzed = new ArrayList<>();
                    count++;
                    synchronized (LOGGER) {
                        LOGGER.info("Analyzing: " + e.tgt().getDeclaringClass() + "." + e.tgt().getName());
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




