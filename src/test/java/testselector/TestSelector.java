package testselector;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.SootMethod;
import testselector.exception.InvalidTargetPaths;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;
import testselector.project.NewProject;
import testselector.project.PreviousProject;
import testselector.project.Project;
import testselector.testselector.FromTheBottom;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class TestSelector {
    private static Set<testselector.testselector.Test> TEST_TO_RUN_FOUND;
    private static Set<SootMethod> TEST_ANALYZED;

    private static Project PREVIOUS_VERSION_PROJECT;
    private static Project NEW_VERSION_PROJECT;
    private static Collection<String> NEW_METHOD_FOUND;
    private static Collection<String> CHANGED_METHOD_FOUND;

    private static final String[] classPath = {"C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar", "C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\rt.jar", "C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\jce.jar", "C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar"};


    @BeforeAll
    static void setUp() throws NoPathException, IOException, NoTestFoundedException, NoNameException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {

        // Sets the package level to INFO

        PREVIOUS_VERSION_PROJECT = new PreviousProject(classPath, "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out" + File.separator + File.separator + "production" + File.separator + File.separator + "p", "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out" + File.separator + File.separator + "test" + File.separator + File.separator + "p");
        try {
            NEW_VERSION_PROJECT = new NewProject(classPath, "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out" + File.separator + File.separator + "production" + File.separator + File.separator + "p1", "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out" + File.separator + File.separator + "test" + File.separator + File.separator + "p1");
        } catch (testselector.exception.InvalidTargetPaths invalidTargetPaths) {
            invalidTargetPaths.printStackTrace();
        }
//        PREVIOUS_VERSION_PROJECT.saveCallGraph("ProjectForTesting", "old");

        FromTheBottom u = new FromTheBottom(PREVIOUS_VERSION_PROJECT, NEW_VERSION_PROJECT);
        TEST_TO_RUN_FOUND = u.selectTest();
        //   NEW_VERSION_PROJECT.saveCallGraph("ProjectForTesting", "new");

        TEST_ANALYZED = null;
        CHANGED_METHOD_FOUND = u.getChangedMethods();
        NEW_METHOD_FOUND = u.getNewMethods();
    }


    @Nested
     class testLoadProject {
        @Test
         void load2ProjectClasses() {
            Assertions.assertFalse(PREVIOUS_VERSION_PROJECT.getProjectClasses().isEmpty());

        }

        @Test
         void loadProjectClasses() {
            Assertions.assertFalse(NEW_VERSION_PROJECT.getProjectClasses().isEmpty());

        }
    }

    @Nested
    class differenceInSetup {

        @Test
         void setUpNotPresent() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("setUp".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);
        }

        @Test
         void presentEqualTest() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("toAddForChangeInSetUpEqual".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);
        }

        @Test
         void presentDifferentTest() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("toAddForChangeInSetUpDifferent".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);
        }

        @Test
         void presentDifferentTestNotAsChangedMethod() {

            for (String s : CHANGED_METHOD_FOUND) {
                assertNotEquals("sootexampleTest.differentTest", s);
            }

        }
    }

    @Nested
    class hierarchyInTestClass{
        @Test
         void testMethodsEredited() {
            int count = 0;
            int clazz1 = 0;
            int clazz2 = 0;
            int clazz0 = 0;
            for (testselector.testselector.Test test : TEST_TO_RUN_FOUND) {
                if ("SuperTest".equals(test.getTestMethod().getName())) {
                    count++;
                    if ("ExtendedClass2".equals(test.getTestMethod().getDeclaringClass().getShortName()))
                        clazz1++;
                    if ("ExtendedClass1".equals(test.getTestMethod().getDeclaringClass().getShortName()))
                        clazz2++;
                    if ("SuperTestClass".equals(test.getTestMethod().getDeclaringClass().getShortName()))
                        clazz0++;

                }

            }
            assertEquals(3, count);
            assertEquals(1, clazz1);
            assertEquals(1, clazz2);
            assertEquals(1, clazz0);
        }
        @Test
         void testInnerTestClass() {
            int count = 0;
            int clazz1 = 0;
            int clazz2 = 0;
            for (testselector.testselector.Test test : TEST_TO_RUN_FOUND) {
                if ("testInInnerClass".equals(test.getTestMethod().getName())) {
                    count++;
                    if ("TestClassWIthInnerClass$1".equals(test.getTestMethod().getDeclaringClass().getShortName()))
                        clazz1++;
                    if ("TestClassWIthInnerClass".equals(test.getTestMethod().getDeclaringClass().getShortName()))
                        clazz2++;

                }

            }
            assertEquals(1, count);
            assertEquals(0, clazz1);
            assertEquals(1, clazz2);
        }

        @Test
         void testInnerTestClass2() {
            int count = 0;
            boolean check = false;
            for (testselector.testselector.Test test : TEST_TO_RUN_FOUND) {
                if ("TestClassWIthInnerClass$innerClass".equals(test.getTestMethod().getDeclaringClass().getName())) {
                    count++;
                }
                if("testInInnerClass2".equals(test.getTestMethod().getName())){
                    if("TestClassWIthInnerClass$innerClass".equals(test.getTestMethod().getDeclaringClass().getName()))
                        check = true;

                }

            }
            assertTrue(check);
            assertEquals(1, count);
        }


    @Test
     void testInnerTestClass3() {
        int count = 0;
        boolean check = false;
        for (testselector.testselector.Test test : TEST_TO_RUN_FOUND) {
            if ("TestClassWIthInnerClass$2".equals(test.getTestMethod().getDeclaringClass().getName())) {
                count++;
            }
        }
        assertEquals(1, count);
    }
}

    @Nested
    class abstractTestClass {

        @Nested
        @DisplayName("If more class extend same superClass, the method not-ovverided must be select one time for each leaf classes")
        class testExtendeBy2ClassdAndNotOverride {

            @Test
             void concreteTestMethodNotOverrideByAnyClassCoverageDifference() {
                int count = 0;
                int clazz1 = 0;
                int clazz2 = 0;
                int clazz0 = 0;
                for (testselector.testselector.Test test : TEST_TO_RUN_FOUND) {
                    if ("concreteMethodThatTestDifferentMethod".equals(test.getTestMethod().getName())) {
                        count++;
                        if ("ExtendedAbstractClass2".equals(test.getTestMethod().getDeclaringClass().getShortName()))
                            clazz1++;
                        if ("ExtendedAbstractClass".equals(test.getTestMethod().getDeclaringClass().getShortName()))
                            clazz2++;
                        if ("AbstractTestClass".equals(test.getTestMethod().getDeclaringClass().getShortName()))
                            clazz0++;

                    }

                }
                assertEquals(2, count);
                assertEquals(1, clazz1);
                assertEquals(1, clazz2);
                assertEquals(0, clazz0);
            }



            @Test
            @Disabled("The new version of the algorithm not analyzed all tests, but only the tests that cover changed or new methods")
             void concreteTestMethodNotOverrideByAnyClass() {
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
        @Disabled("The new version of the algorithm not analyzed all tests, but only the tests that cover changed or new methods")
        class testMethodOverride {
            @Test
             void concreteTestMethodOverrideBy1ClassAnd1Not() {
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
             void abstractTest() {
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
             void concreteMethodOverrided() {
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
             void concreteMethodOverridedThatCoverageDifference() {
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

    @Nested
    @DisplayName("check if the all graph is analyzed")
    class differenceInAPrivateMethod {
        @Test
         void utilTestDifferenceInAPrivateMethod() {
            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testDifferenceInAPrivateMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);
        }

        @Test
         void utilTestFindChangeInAPrivateMethod() {
            AtomicBoolean check = new AtomicBoolean(false);
            for (String value : CHANGED_METHOD_FOUND) {

                if (value.equals("sootTest.sootexample.privateMethodWithChange"))
                    check.set(true);

            }
            assertTrue(check.get());
        }
    }

    @Nested
    @DisplayName("check that a method that have changed signature is considerate as new method")
            //Falso. Non compare tra i metodi differenti solo perchè l'analisi si ferma prima di arrivare ad esso.
    class differentSignature {

        @Test
        @DisplayName("method with changed signature ia not a new method")
         void testFindChangeInSignature() {
            AtomicBoolean check = new AtomicBoolean(false);
            for (String value : CHANGED_METHOD_FOUND) {

                if (value.equals("sootTest.sootexample.methodWithDifferentSignature"))
                    check.set(true);

            }
            assertTrue(check.get());
        }

        @Test
        @DisplayName("method that call a method with changed signature is a modified method")
         void testFindChangeInSignature2() {
            AtomicBoolean check = new AtomicBoolean(false);
            for (String value : CHANGED_METHOD_FOUND) {

                if (value.equals("sootTest.sootexample.differenceInSignature"))
                    check.set(true);

            }
            assertTrue(check.get());
        }

        @Test
        @DisplayName("the test that cover a method that call a method with changed signature must be selected")
         void testChangeInSignature() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testDifferenceInSignature".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);
        }
    }

    @Nested
    @DisplayName("a method that is different only for a different name of a variable must not be marked as different")
    class methodWithDifferentNameOfAVariable {
        @Test
         void testDifferentNameOfAVariable() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testDifferentNameOfAVariable".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);
        }

        @Test
         void testFindDifferentInNameOfAVariable() {
            AtomicBoolean check = new AtomicBoolean(false);

            for (String value : CHANGED_METHOD_FOUND) {

                if (value.equals("sootTest.sootexample.methodWithDifferenceInVariableName"))
                    check.set(true);

            }
            assertFalse(check.get());
        }

    }


    @Nested
    @DisplayName("find new methods and their test")
    class newMethodAndTheirTest {
        @Test
        public void newMethodTest() {

            AtomicBoolean check = new AtomicBoolean(false);
            Iterator<String> listIterator = NEW_METHOD_FOUND.iterator();
            while (listIterator.hasNext()) {
                String value = listIterator.next();
                if (value.equals("sootTest.sootexample.newMethod"))
                    check.set(true);


            }
            assertTrue(check.get());


            check.set(false);


            listIterator = CHANGED_METHOD_FOUND.iterator();
            while (listIterator.hasNext()) {
                String value = listIterator.next();

                if (value.equals("newMethod"))
                    check.set(true);


            }
            assertFalse(check.get());


        }

        @Test
        public void newMethodCheckTest() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testNewMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);


        }

    }


    @Nested
    class staticMethod {
        @Test
        public void staticTest() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testStaticDifferentMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);


        }

        @Test
        public void staticTestForEqualsMethod() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testStaticEqualMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);


        }
    }

    @Nested
    @DisplayName("if a class have difference in constant the tests cover that class must be selected")
    class testDifferenceInConstant {
        @Test
        public void constantTest() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testField".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);


        }
    }

    @Nested
    class testFinalConstantsAndMethods {
        @Test
        public void finalTest() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testFinalDifferentMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);


        }

        @Test
        public void finalStaticTest() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testFinalStaticDifferentMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);


        }

        @Test
        public void finalTestForEqualsMethod() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testFinalEqualMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);


        }

        @Test
        public void finalStaticTestForEqualsMethod() {

            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testFinalStaticEqualMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);


        }

    }

    @Nested
    class testHierarchy {
        @Test
        public void testChangedHierarchy() {
            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testChangedHierarchy".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);

        }

        @Test
        public void testEqualHierarchy() {
            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testEqualHierarchy".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);


        }


        @Test
        public void test1MethodEqualHierarchy() {
            boolean check = false;
            for (String method : CHANGED_METHOD_FOUND) {
                if ("sootTest.FirstClass.fooEqual".equals(method))
                    check = true;
            }
            assertFalse(check);
        }

        @Test
        public void test1MethodChangedHierarchy() {
            boolean check = false;
            for (String method : CHANGED_METHOD_FOUND) {
                if ("sootTest.FirstClass.foo".equals(method)) ;
                check = true;
            }
            assertTrue(check);

        }

        @Test
        public void test2MethodEqualHierarchy() {
            boolean check = false;
            for (String method : CHANGED_METHOD_FOUND) {
                if ("sootTest.SecondClass.fooEqual".equals(method))
                    check = true;
            }
            assertFalse(check);
        }

        @Test
        public void test2MethodChangedHierarchy() {
            boolean check = false;
            for (String method : CHANGED_METHOD_FOUND) {
                if ("sootTest.ThirdClass.foo".equals(method)) ;
                check = true;
            }
            assertTrue(check);

        }

        @Test
        public void test3MethodEqualHierarchy() {
            boolean check = false;
            for (String method : CHANGED_METHOD_FOUND) {
                if ("sootTest.ThirdClass.fooEqual".equals(method))
                    check = true;
            }
            assertFalse(check);
        }

    }

    @Nested
    class changedInClinit {
        @Test
        public void test1MethodChangedInClinit() {
            boolean check = false;
            for (String methodString : CHANGED_METHOD_FOUND) {
                if ("sootTest.object.getStaticField".equals(methodString))
                    check = true;
            }
            assertTrue(check);


        }

        @Test
        public void test2MethodChangedInClinit() {
            boolean check = false;
            for (String methodString : CHANGED_METHOD_FOUND) {
                if ("sootTest.object.getNormalField".equals(methodString))
                    check = true;
            }
            assertTrue(check);

        }


        @Test
        public void testEqualFieldInAClassWithDifferentClinit() {
            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testField".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);


        }

        @Test
        public void testDifferentFieldInAClassWithDifferentClinit() {
            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testGetStaticField".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);


        }

        @Test
        public void testEqualMethodInAClassWithDifferentClinit() {
            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("testFoo".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);

        }

    }

    @Nested
    public class changedTest {
        @Test
        public void testChangedTest() {
            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("differentTest".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);

        }

        @Test
        public void testEqualTest() {
            boolean check = false;
            for (testselector.testselector.Test t : TEST_TO_RUN_FOUND) {
                if ("equalTest".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);

        }

        @Test
        public void testChangedTestNotInDifferentMethods() {
            boolean check = false;
            for (String method : CHANGED_METHOD_FOUND) {
                if ("test.sootexampleTest.differentTest".equals(method))
                    check = true;
            }
            assertFalse(check);
        }

    }


    @Nested
    public class testCoverTwoChangedMethods {
        //Da capire. Qualche volta fallisce, qqualche volta non fallisce. -> dipende dal ml.

        @Test
        public void testCover2ChangedMethods() {

            for (testselector.testselector.Test test : TEST_TO_RUN_FOUND) {
                if ("test2ChangedMethod".equals(test.getTestMethod().getName())) {
                    assertFalse(test.getTestingMethods().contains("sootTest.sootexample.secondMethodTested") && test.getTestingMethods().contains("sootTest.sootexample.oneMethodTested"));
                    assertTrue(test.getTestingMethods().contains("sootTest.sootexample.secondMethodTested") || test.getTestingMethods().contains("sootTest.sootexample.oneMethodTested"));
                }
            }

        }
    }

    @Nested
    public class testSetUpCall {

        @Test
        public void testSetUpCall() {
            int count = 0;
            for (testselector.testselector.Test test : TEST_TO_RUN_FOUND) {
                if ("setUpCallDM".equals(test.getTestMethod().getDeclaringClass().getName())) {

                    if ("test_1".equals(test.getTestMethod().getName()))
                        count++;
                    if ("test_2".equals(test.getTestMethod().getName()))
                        count++;
                    if ("test_3".equals(test.getTestMethod().getName()))
                        count++;


                }
            }
            assertEquals(3, count);


        }
    }

    @Nested
    public class testTearDownCall {

        @Test
        public void testTearDownCall() {
            int count = 0;
            for (testselector.testselector.Test test : TEST_TO_RUN_FOUND) {
                if ("tearDownCallDM".equals(test.getTestMethod().getDeclaringClass().getName())) {

                    if ("test_1".equals(test.getTestMethod().getName()))
                        count++;
                    if ("test_2".equals(test.getTestMethod().getName()))
                        count++;
                    if ("test_3".equals(test.getTestMethod().getName()))
                        count++;


                }
            }
            assertEquals(3, count);


        }
    }

    @Nested
     class testInit {

        @Test
         void testInitCallDM() {
            int count = 0;
            for (testselector.testselector.Test test : TEST_TO_RUN_FOUND) {
                if ("InitTestClass".equals(test.getTestMethod().getDeclaringClass().getName())) {

                    if ("test".equals(test.getTestMethod().getName()))
                        count++;

                }
            }
            assertEquals(1, count);


        }
    }

    @Nested
     class testTestCallAnotherTest {

        @Test
         void testTestCallAnotherTest() {
            int count = 0;
            for (testselector.testselector.Test test : TEST_TO_RUN_FOUND) {
                if ("sootexampleTest".equals(test.getTestMethod().getDeclaringClass().getName())) {

                    if ("testCallAnotherTest_1".equals(test.getTestMethod().getName()))
                        count++;
                    if ("testCallAnotherTest_2".equals(test.getTestMethod().getName()))
                        count++;
                    if ("testCallAnotherTest_3".equals(test.getTestMethod().getName()))
                        count++;

                }
            }
            assertEquals(3, count);


        }
    }

    @Nested
     class testStaticAnalyses {

        @Test
        @Disabled("will fail: for the static analyses of the code, see issue #14: https://gitlab.com/DarioAmorosodAragona/whatTests/issues/14")

        public void testStaticAnalyses() {
            int count = 0;
            for (testselector.testselector.Test test : TEST_TO_RUN_FOUND) {
                if ("TestStaticAnalyses".equals(test.getTestMethod().getDeclaringClass().getName())) {
                    count++;

                }
            }
            assertEquals(1, count);


        }
    }
}



