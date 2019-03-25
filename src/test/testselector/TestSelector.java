
import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.*;
import soot.SootMethod;
import testSelector.project.NewProject;
import testSelector.project.PreviousProject;
import testSelector.project.Project;
import testSelector.testSelector.OnlyOneGrapMultiThread;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;

import java.io.File;
import java.nio.file.NotDirectoryException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class TestSelector {
    private static Set<testSelector.testSelector.Test> TEST_TO_RUN_FINDED;
    private static Set<SootMethod> TEST_ANALYZED;

    private static Project PREVIOUS_VERSION_PROJECT;
    private static Project NEW_VERSION_PROJECT;
    private static Collection<Set<String>> NEW_METHOD_FINDED;
    private static Collection<Set<String>> CHANGED_METHOD_FINDED;
    private static final String[] classPath = {"C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\jce.jar;C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar"};


    @BeforeAll
    public static void setUp() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        BasicConfigurator.configure();

        PREVIOUS_VERSION_PROJECT = new PreviousProject(4, classPath, "out" + File.separator + File.separator + "production" + File.separator + File.separator + "p");
        NEW_VERSION_PROJECT = new NewProject( 4, classPath, "out" + File.separator + File.separator + "production" + File.separator + File.separator + "p1");
//        PREVIOUS_VERSION_PROJECT.saveCallGraph("ProjectForTesting", "old");

        OnlyOneGrapMultiThread u = new OnlyOneGrapMultiThread(PREVIOUS_VERSION_PROJECT, NEW_VERSION_PROJECT, true);
        TEST_TO_RUN_FINDED = u.selectTest();
        NEW_VERSION_PROJECT.saveCallGraph("ProjectForTesting", "new");

        TEST_ANALYZED = u.getAllMethodsAnalyzed();
        CHANGED_METHOD_FINDED = u.getChangedMethods();
        NEW_METHOD_FINDED = u.getNewOrRemovedMethods();
    }

    @Test
    public void load2ProjectClasses() {
        assertTrue(!PREVIOUS_VERSION_PROJECT.getProjectClasses().isEmpty());

    }

    @Test
    public void loadProjectClasses() {
        assertTrue(!NEW_VERSION_PROJECT.getProjectClasses().isEmpty());

    }

    @Nested
    class differenceInSetup {

        @Test
        public void setUpNotPresent() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
                if ("setUp".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);
        }

        @Test
        public void presentEqualTest() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
                if ("toAddForChangeInSetUpEqual".equals(t.getTestMethod().getName()))
                    check = true;
            }
      assertTrue(check);
        }

        @Test
        public void presentDifferentTest() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
                if ("toAddForChangeInSetUpDifferent".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);
        }

    }

    @Nested
    class abstractTestClass {

        @Nested
        @DisplayName("If more class extend same superClass, the method not-ovveride must be select only one time")
        class testExtendeBy2ClassdAndNotOverride {

            @Test
            public void concreteTestMethodNotOverrideByAnyClassCoverageDifference() {
                int count = 0;
                for (testSelector.testSelector.Test test : TEST_TO_RUN_FINDED) {
                    if ("concreteMethodThatTestDifferentMethod".equals(test.getTestMethod().getName()))
                        count++;
                }
                assertEquals(1, count);
            }

            @Test
            public void concreteTestMethodNotOverrideByAnyClass() {
                int count = 0;
                for (SootMethod test : TEST_ANALYZED) {
                    if ("concreteMethodNotOverrided".equals(test.getName()))
                        count++;
                }
                assertEquals(1, count);
            }


        }

        @Nested
        @DisplayName("If a test method is override by a one or more class, that test must be analyzed but not also superclass's version of that method")
        class testMethodOverride {
            @Test
            public void concreteTestMethodOverrideBy1ClassAnd1Not() {
                int count = 0;
                int sameClass = 0;
                for (SootMethod test : TEST_ANALYZED) {
                    if ("concreteMethodOverridedOnlyByExtendAbstractClass2".equals(test.getName())) {
                        count++;
                        if (test.getDeclaringClass().getJavaStyleName().equals("ExtendedAbstractClass2"))
                            sameClass++;
                    }
                }
                assertEquals(2, count);
                assertEquals(1, sameClass);
            }

            @Test
            public void abstractTest() {
                int count = 0;
                int clazz = 0;
                for (SootMethod test : TEST_ANALYZED) {
                    if ("abstractMethod".equals(test.getName())) {
                        count++;
                        if (test.getDeclaringClass().getJavaStyleName().equals("ExtendedAbstractClass2"))
                            clazz++;
                        if (test.getDeclaringClass().getJavaStyleName().equals("ExtendedAbstractClass"))
                            clazz++;
                    }
                }
                assertEquals(2, count);
                assertEquals(2, clazz);
            }

            @Test
            public void concreteMethodOverrided() {
                int count = 0;
                int clazz = 0;
                for (SootMethod test : TEST_ANALYZED) {
                    if ("concreteMethodOverrided".equals(test.getName())) {
                        count++;
                        if (test.getDeclaringClass().getJavaStyleName().equals("ExtendedAbstractClass2"))
                            clazz++;
                        if (test.getDeclaringClass().getJavaStyleName().equals("ExtendedAbstractClass"))
                            clazz++;
                    }
                }
                assertEquals(2, count);
                assertEquals(2, clazz);
            }
            @Test
            public void concreteMethodOverridedThatCoverageDifference() {
                int count = 0;
                int clazz = 0;
                for (SootMethod test : TEST_ANALYZED) {
                    if ("concreteMethodOverrided".equals(test.getName())) {
                        count++;
                        if (test.getDeclaringClass().getJavaStyleName().equals("ExtendedAbstractClass2"))
                            clazz++;
                        if (test.getDeclaringClass().getJavaStyleName().equals("ExtendedAbstractClass"))
                            clazz++;
                    }
                }
                assertEquals(2, count);
                assertEquals(2, clazz);
            }


        }
    }


    @Test
    public void utilTestDifferenceInAPrivateMethod() {
        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
            if ("testDifferenceInAPrivateMethod".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertTrue(check);
    }

    @Test
    public void utilTestFindChangeInAPrivateMethod() {
        AtomicBoolean check = new AtomicBoolean(false);
        for (Set<String> value : CHANGED_METHOD_FINDED) {
            value.forEach(method -> {
                if (method.equals("sootTest.sootexample.privateMethodWithChange"))
                    check.set(true);
            });

        }
     assertTrue(check.get());
    }


    @Test
    //Questo test deve essere false poichè un metodo con una signature diversa diventa come un metodo nuovo, quindi non viene selezionato, poichè
    //per ora selezioniamo solo quei test che testano metodi già presenti in PREVIOUS_VERSION_PROJECT ma modificati, diversa signature = metodo diverso = in PREVIOUS_VERSION_PROJECT non c'è = non selezionato.
    public void utilTestFindChangeInSignature() {
        AtomicBoolean check = new AtomicBoolean(false);
        for (Set<String> value : CHANGED_METHOD_FINDED) {
            value.forEach(method -> {
                if (method.equals("methodWithDifferentSignature"))
                    check.set(true);
            });
        }
     assertFalse(check.get());
    }

    @Test
    public void utilTestFindChangeInSignature2() {
        AtomicBoolean check = new AtomicBoolean(false);
        for (Set<String> value : CHANGED_METHOD_FINDED) {
            value.forEach(method -> {
                if (method.equals("sootTest.sootexample.differenceInSignature"))
                    check.set(true);
            });
        }
     assertTrue(check.get());
    }

    @Test
    //Perchè non fallisce? Poichè il metodo che chiama "methodWithDifferentSignature", chiama questo metodo in PREVIOUS_VERSION_PROJECT in un modo (virtual invoke) e in NEW_VERSION_PROJECT in un altro modo (special invoke)
    //quindi la differenza nella signature del metodo che in PREVIOUS_VERSION_PROJECT è "protected" mentre in NEW_VERSION_PROJECT è "private" cambia come questo metodo ciene invocato e di conseguenza anche il metodo stesso che chiama questo metodo risulta diverso in PREVIOUS_VERSION_PROJECT e NEW_VERSION_PROJECT.
    //QUindi testo anhe i metodi che chiamano altri metodi a cui è stata cambiata a signature.
    public void utilTestChangeInSignature() {

        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
            if ("testDifferenceInSignature".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertTrue(check);
    }

    @Test
    public void utilTestDifferentNameOfAVariable() {

        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
            if ("testDifferentNameOfAVariable".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertFalse(check);
    }

    @Test
    public void utilTestFindDifferentInNameOfAVariable() {
        AtomicBoolean check = new AtomicBoolean(false);

        for (Set<String> value : CHANGED_METHOD_FINDED) {
            value.forEach(method -> {
                if (method.equals("methodWithDifferenceInVariableName"))
                    check.set(true);
            });
        }
     assertFalse(check.get());
    }

    @Test
    @Disabled
    public void newMethodTest() {

        AtomicBoolean check = new AtomicBoolean(false);
        Iterator<Set<String>> listIterator = NEW_METHOD_FINDED.iterator();
        while (listIterator.hasNext()) {
            HashSet<String> value = (HashSet<String>) listIterator.next();
            value.forEach(method -> {
                if (method.equals("newMethod"))
                    check.set(true);

            });


        }
        assertTrue(check.get());


        check.set(false);


        listIterator = CHANGED_METHOD_FINDED.iterator();
        while (listIterator.hasNext()) {
            HashSet<String> value = (HashSet<String>) listIterator.next();
            value.forEach(method -> {
                if (method.equals("newMethod"))
                    check.set(true);

            });

        }
        assertFalse(check.get());


    }

    @Test
    @Disabled
    public void newMethodCheckTest() {

        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
            if ("testNewMethod".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertTrue(check);


    }

    @Test
    public void constantTest() {

        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
            if ("testField".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertTrue(check);


    }

    @Test
    public void finalTest() {

        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
            if ("testFinalDifferentMethod".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertTrue(check);


    }

    @Test
    public void finalStaticTest() {

        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
            if ("testFinalStaticDifferentMethod".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertTrue(check);


    }

    @Test
    public void staticTest() {

        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
            if ("testStaticDifferentMethod".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertTrue(check);


    }

    @Test
    public void finalTestForEqualsMethod() {

        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
            if ("testFinalEqualMethod".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertFalse(check);


    }

    @Test
    public void finalStaticTestForEqualsMethod() {

        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
            if ("testFinalStaticEqualMethod".equals(t.getTestMethod().getName()))
                check = true;
        }
       assertFalse(check);


    }

    @Test
    public void staticTestForEqualsMethod() {

        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FINDED) {
            if ("testStaticEqualMethod".equals(t.getTestMethod().getName()))
                check = true;
        }
       assertFalse(check);


    }

}
