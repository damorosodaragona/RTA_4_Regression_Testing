//package testselector;
//
//import org.apache.log4j.BasicConfigurator;
//import org.junit.Assert;
//import testselector.exception.NoNameException;
//import testselector.exception.NoPathException;
//import testselector.exception.NoTestFoundedException;
//import testselector.project.Project;
//import testselector.testSelector.Test;
//
//import java.io.File;
//import java.lang.reflect.Method;
//import java.nio.file.NotDirectoryException;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class TestSelector {
//    private static Set<Test> TEST_TO_RUN_FINDED;
//    private static Project PREVIOUS_VERSION_PROJECT;
//    private static Project NEW_VERSION_PROJECT;
//    private static Collection<Set<Method>> NEW_METHOD_FINDED;
//    private static Collection<Set<Method>> CHANGED_METHOD_FINDED;
//    private static final String[] classPath = {"C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\jce.jar;C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar"};
//
//
//    @org.junit.BeforeClass
//    public static void setUp() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
//        BasicConfigurator.configure();
//
//        PREVIOUS_VERSION_PROJECT = new Project(classPath, "out" + File.separator + File.separator + "production" + File.separator + File.separator + "p");
//        NEW_VERSION_PROJECT = new Project(classPath, "out" + File.separator + File.separator + "production" + File.separator + File.separator + "p1");
//        PREVIOUS_VERSION_PROJECT.saveCallGraph("ProjectForTesting", "old");
//        NEW_VERSION_PROJECT.saveCallGraph("ProjectForTesting", "new");
//
//        testselector.testSelector.TestSelector u = new testselector.testSelector.TestSelector(PREVIOUS_VERSION_PROJECT, NEW_VERSION_PROJECT, true);
//        TEST_TO_RUN_FINDED = u.selectTest();
//        CHANGED_METHOD_FINDED = u.getChangedMethods();
//        NEW_METHOD_FINDED = u.getNewOrRemovedMethods();
//    }
//
//    @org.junit.Test
//    public void load2ProjectClasses() {
//        Assert.assertTrue(!PREVIOUS_VERSION_PROJECT.getProjectClasses().isEmpty());
//
//    }
//
//    @org.junit.Test
//    public void loadProjectClasses() {
//        Assert.assertTrue(!NEW_VERSION_PROJECT.getProjectClasses().isEmpty());
//
//    }
//
//    @org.junit.Test
//    public void utilTestDifferenceInAPrivateMethod() {
//        boolean check = false;
//        for (Test t : TEST_TO_RUN_FINDED) {
//            if ("testDifferenceInAPrivateMethod".equals(t.getTestMethod().getName()))
//                check = true;
//        }
//        Assert.assertTrue(check);
//    }
//
//    @org.junit.Test
//    //Non lo so.
//    public void utilTestFindChangeInAPrivateMethod() {
//        AtomicBoolean check = new AtomicBoolean(false);
//        for (Set<Method> value : CHANGED_METHOD_FINDED) {
//            value.forEach(method -> {
//                if (method.getName().equals("privateMethodWithChange"))
//                    check.set(true);
//            });
//
//        }
//        Assert.assertTrue(check.get());
//    }
//
//
//    @org.junit.Test
//    //Questo test deve essere false poichè un metodo con una signature diversa diventa come un metodo nuovo, quindi non viene selezionato, poichè
//    //per ora selezioniamo solo quei test che testano metodi già presenti in PREVIOUS_VERSION_PROJECT ma modificati, diversa signature = metodo diverso = in PREVIOUS_VERSION_PROJECT non c'è = non selezionato.
//    public void utilTestFindChangeInSignature() {
//        AtomicBoolean check = new AtomicBoolean(false);
//        for (Set<Method> value : CHANGED_METHOD_FINDED) {
//            value.forEach(method -> {
//            if (method.getName().equals("methodWithDifferentSignature"))
//                check.set(true);
//            });
//        }
//        Assert.assertTrue(check.get());
//    }
//
//
//    @org.junit.Test
//    //Perchè non fallisce? Poichè il metodo che chiama "methodWithDifferentSignature", chiama questo metodo in PREVIOUS_VERSION_PROJECT in un modo (virtual invoke) e in NEW_VERSION_PROJECT in un altro modo (special invoke)
//    //quindi la differenza nella signature del metodo che in PREVIOUS_VERSION_PROJECT è "protected" mentre in NEW_VERSION_PROJECT è "private" cambia come questo metodo ciene invocato e di conseguenza anche il metodo stesso che chiama questo metodo risulta diverso in PREVIOUS_VERSION_PROJECT e NEW_VERSION_PROJECT.
//    //QUindi testo anhe i metodi che chiamano altri metodi a cui è stata cambiata a signature.
//    public void utilTestChangeInSignature() {
//
//        boolean check = false;
//        for (Test t : TEST_TO_RUN_FINDED) {
//            if ("testDifferenceInSignature".equals(t.getTestMethod().getName()))
//                check = true;
//        }
//        Assert.assertTrue(check);
//    }
//
//    @org.junit.Test
//
//    public void utilTestDifferentNameOfAVariable() {
//
//        boolean check = false;
//        for (Test t : TEST_TO_RUN_FINDED) {
//            if ("testDifferentNameOfAVariable".equals(t.getTestMethod().getName()))
//                check = true;
//        }
//        Assert.assertFalse(check);
//    }
//
//    @org.junit.Test
//
//    public void utilTestFindDifferentInNameOfAVariable() {
//        AtomicBoolean check = new AtomicBoolean(false);
//
//        for (Set<Method> value : CHANGED_METHOD_FINDED) {
//            value.forEach(method -> {
//                if (method.getName().equals("methodWithDifferenceInVariableName"))
//                    check.set(true);
//            });
//        }
//        Assert.assertFalse(check.get());
//    }
//
//    @org.junit.Test
//    public void newMethodTest() {
//
//        AtomicBoolean check = new AtomicBoolean(false);
//        Iterator<Set<Method>> listIterator = NEW_METHOD_FINDED.iterator();
//        while (listIterator.hasNext()) {
//            HashSet<Method> value = (HashSet<Method>) listIterator.next();
//            value.forEach(method -> {
//                if (method.getName().equals("newMethod"))
//                    check.set(true);
//
//            });
//
//
//        }
//        Assert.assertTrue(check.get());
//
//
//        check.set(false);
//
//
//
//        listIterator = CHANGED_METHOD_FINDED.iterator();
//        while (listIterator.hasNext()) {
//            HashSet<Method> value = (HashSet<Method>) listIterator.next();
//            value.forEach(method -> {
//                if (method.getName().equals("newMethod"))
//                    check.set(true);
//
//            });
//
//        }
//        Assert.assertFalse(check.get());
//
//
//    }
//
//    @org.junit.Test
//    public void newMethodCheckTest() {
//
//        boolean check = false;
//        for (Test t : TEST_TO_RUN_FINDED) {
//            if ("testNewMethod".equals(t.getTestMethod().getName()))
//                check = true;
//        }
//        Assert.assertTrue(check);
//
//
//    }
//
//    @org.junit.Test
//    public void constantTest() {
//
//        boolean check = false;
//        for (Test t : TEST_TO_RUN_FINDED) {
//            if ("testField".equals(t.getTestMethod().getName()))
//                check = true;
//        }
//        Assert.assertTrue(check);
//
//
//    }
//
//    @org.junit.Test
//    public void finalTest() {
//
//        boolean check = false;
//        for (Test t : TEST_TO_RUN_FINDED) {
//            if ("testFinalDifferentMethod".equals(t.getTestMethod().getName()))
//                check = true;
//        }
//        Assert.assertTrue(check);
//
//
//    }
//
//    @org.junit.Test
//    public void finalStaticTest() {
//
//        boolean check = false;
//        for (Test t : TEST_TO_RUN_FINDED) {
//            if ("testFinalStaticDifferentMethod".equals(t.getTestMethod().getName()))
//                check = true;
//        }
//        Assert.assertTrue(check);
//
//
//    }
//
//    @org.junit.Test
//    public void staticTest() {
//
//        boolean check = false;
//        for (Test t : TEST_TO_RUN_FINDED) {
//            if ("testStaticDifferentMethod".equals(t.getTestMethod().getName()))
//                check = true;
//        }
//        Assert.assertTrue(check);
//
//
//    }
//
//    @org.junit.Test
//    public void finalTestForEqualsMethod() {
//
//        boolean check = false;
//        for (Test t : TEST_TO_RUN_FINDED) {
//            if ("testFinalEqualMethod".equals(t.getTestMethod().getName()))
//                check = true;
//        }
//        Assert.assertFalse(check);
//
//
//    }
//
//    @org.junit.Test
//    public void finalStaticTestForEqualsMethod() {
//
//        boolean check = false;
//        for (Test t : TEST_TO_RUN_FINDED) {
//            if ("testFinalStaticEqualMethod".equals(t.getTestMethod().getName()))
//                check = true;
//        }
//        Assert.assertFalse(check);
//
//
//    }
//
//    @org.junit.Test
//    public void staticTestForEqualsMethod() {
//
//        boolean check = false;
//        for (Test t : TEST_TO_RUN_FINDED) {
//            if ("testStaticEqualMethod".equals(t.getTestMethod().getName()))
//                check = true;
//        }
//        Assert.assertFalse(check);
//
//
//    }
//
//}
