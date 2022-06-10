package CATTO.code.analyzer;

import CATTO.project.NewProject;
import CATTO.project.PreviousProject;
import CATTO.test.Test;
import CATTO.util.JunitUtil;
import org.apache.log4j.Logger;
import soot.Modifier;
import soot.SootClass;
import soot.SootMethod;

import java.util.*;

public class CodeAnalyzer {
    private final HashSet<SootMethod> equalsMethods;
    private final HashSet<Test> differentTest;
    private final HashSet<SootClass> differentObject;
    private final HashSet<SootMethod> newMethods;
    NewProject newProjectVersion;
    PreviousProject previousProjectVersion;
    private final HashSet<SootMethod> differentMethods;
    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(CodeAnalyzer.class);

    public CodeAnalyzer(NewProject newProjectVersion, PreviousProject previousProjectVersion){
        this.newProjectVersion = newProjectVersion;
        this.previousProjectVersion = previousProjectVersion;
        this.differentMethods = new HashSet<>();
        this.equalsMethods = new HashSet<>();
        this.differentTest = new HashSet<Test>();
        this.differentObject = new HashSet<SootClass>();
        this.newMethods = new HashSet<>();

    }

    public void analyze(){
        LOGGER.info("searching changes in hierarchies");
        findDifferenceInHierarchy();
        LOGGER.info("searching modified methods");
        findDifferentMethods();
        LOGGER.info("searching new methods");
        findNewMethods();
        LOGGER.info("searching different tests");
        comparingTest();
        LOGGER.info("comparing classes (to check if the constructors are equals)");
        isTheSameObject();
    }

    public HashSet<SootClass> getDifferentObject() {
        return differentObject;
    }

    public HashSet<SootMethod> getDifferentMethods() {
        return differentMethods;
    }

    public HashSet<Test> getDifferentTest() {
        return differentTest;
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
                        if (Modifier.isAbstract(m1.getModifiers())) {
                            equalsMethods.add(m1);
                            continue;
                        }
//                        // mi assicuro che il metodo che sto confrontando non sia il metodo della classe madre ma quello della classe figlia
//
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
        boolean toAdd = false;
        for (SootMethod testMethod : differentMethods) {

            toAdd = ((testMethod.getName().equals("<init>") || testMethod.getName().equals("<clinit>")) && JunitUtil.isATestClass(testMethod));


            if (!toAdd && JunitUtil.isATestMethod(testMethod)) {
                if (Modifier.isAbstract(testMethod.getDeclaringClass().getModifiers())) {
                    toDelete.add(testMethod);
                    continue;
                }
                toAdd = JunitUtil.isSetup(testMethod) || JunitUtil.isTearDown(testMethod);
                if (!toAdd && JunitUtil.isJunitTestCase(testMethod)) {
                    //aggiungo ai test differenti solo i test -> metodi con @Test. I @Before,@After ecc ecc verrano eseguiti lo stesso

                    LOGGER.info("The test: " + testMethod.getDeclaringClass().getName() + "." + testMethod.getName() + " has been added because it is in both versions of the project but has been changed");
                    differentTest.add(new Test(testMethod));


                }
            }
            if (toAdd)
                for (SootMethod s : testMethod.getDeclaringClass().getMethods()) {
                    if (JunitUtil.isJunitTestCase(s)) {
                        differentTest.add(new Test(s));
                    }
                }
        }

        differentTest.forEach(test -> differentMethods.remove(test.getTestMethod()));
        toDelete.forEach(differentMethods::remove);

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
    public Collection<String> getStringNewMethods() {

        Collection<String> newMethodsCopy = new ArrayList<>();
        newMethods.forEach(newMethod -> newMethodsCopy.add(newMethod.getDeclaringClass().getName() + "." + newMethod.getName()));
        return newMethodsCopy;
    }


    public HashSet<SootMethod> getNewMethods() {

        return newMethods;
    }
}
