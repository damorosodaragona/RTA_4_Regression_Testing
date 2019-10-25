import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import soot.SootMethod;
import testSelector.project.NewProject;
import testSelector.project.PreviousProject;
import testSelector.project.Project;
import testSelector.testSelector.FromTheBottom;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;

import java.io.File;
import java.nio.file.NotDirectoryException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestSelectorJUnit4 {
    private static Set<testSelector.testSelector.Test> TEST_TO_RUN_FOUND;
    private static Set<SootMethod> TEST_ANALYZED;

    private static Project PREVIOUS_VERSION_PROJECT;
    private static Project NEW_VERSION_PROJECT;
    private static Collection<String> NEW_METHOD_FINDED;
    private static Collection<String> CHANGED_METHOD_FINDED;
    private static final String[] classPath = {"C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\jce.jar;C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar"};


    @BeforeClass
    public static void setUp() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        BasicConfigurator.configure();

        PREVIOUS_VERSION_PROJECT = new PreviousProject(4, classPath, "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out" + File.separator + File.separator + "production" + File.separator + File.separator + "p");
        NEW_VERSION_PROJECT = new NewProject(4, classPath, "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out" + File.separator + File.separator + "production" + File.separator + File.separator + "p1");
//        PREVIOUS_VERSION_PROJECT.saveCallGraph("ProjectForTesting", "old");

        FromTheBottom u = new FromTheBottom(PREVIOUS_VERSION_PROJECT, NEW_VERSION_PROJECT, true);
        TEST_TO_RUN_FOUND = u.selectTest();
        NEW_VERSION_PROJECT.saveCallGraph("ProjectForTesting", "new");

        TEST_ANALYZED = null;
        CHANGED_METHOD_FINDED = u.getChangedMethods();

        NEW_METHOD_FINDED = u.getNewMethods();
    }


        @Test
        public void load2ProjectClasses() {
            assertTrue(!PREVIOUS_VERSION_PROJECT.getProjectClasses().isEmpty());

        }

        @Test
        public void loadProjectClasses() {
            assertTrue(!NEW_VERSION_PROJECT.getProjectClasses().isEmpty());

        }



        @Test
        public void setUpNotPresent() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("setUp".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);
        }

        @Test
        public void presentEqualTest() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("toAddForChangeInSetUpEqual".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);
        }

        @Test
        public void presentDifferentTest() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("toAddForChangeInSetUpDifferent".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);
        }







            @Test
            public void concreteTestMethodNotOverrideByAnyClassCoverageDifference() {
                int count = 0;
                int clazz1 = 0;
                int clazz2 = 0;
                int clazz0 = 0;
                for (testSelector.testSelector.Test test : TEST_TO_RUN_FOUND) {
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
            @Ignore
            public void concreteTestMethodNotOverrideByAnyClass() {
                int count = 0;
                for (SootMethod test : TEST_ANALYZED) {
                    if ("concreteMethodNotOverrided".equals(test.getName()))
                        count++;
                }
                assertEquals(1, count);
            }





            @Test
            @Ignore
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
            @Ignore
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
            @Ignore
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
            @Ignore
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






        @Test
        public void utilTestDifferenceInAPrivateMethod() {
            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("testDifferenceInAPrivateMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);
        }

        @Test
        public void utilTestFindChangeInAPrivateMethod() {
            AtomicBoolean check = new AtomicBoolean(false);
            for (String value : CHANGED_METHOD_FINDED) {

                    if (value.equals("sootTest.sootexample.privateMethodWithChange"))
                        check.set(true);


            }
            assertTrue(check.get());
        }




        @Test
        public void testFindChangeInSignature() {
            AtomicBoolean check = new AtomicBoolean(false);
            for (String value : CHANGED_METHOD_FINDED) {

                    if (value.equals("sootTest.sootexample.methodWithDifferentSignature"))
                        check.set(true);

            }
            assertTrue(check.get());
        }

        @Test
        public void testFindChangeInSignature2() {
            AtomicBoolean check = new AtomicBoolean(false);
            for (String value : CHANGED_METHOD_FINDED) {

                    if (value.equals("sootTest.sootexample.differenceInSignature"))
                        check.set(true);

            }
            assertTrue(check.get());
        }

        @Test
        public void testChangeInSignature() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("testDifferenceInSignature".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);
        }



        @Test
        public void testDifferentNameOfAVariable() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("testDifferentNameOfAVariable".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);
        }

        @Test
        public void testFindDifferentInNameOfAVariable() {
            AtomicBoolean check = new AtomicBoolean(false);

            for (String value : CHANGED_METHOD_FINDED) {

                    if (value.equals("sootTest.sootexample.methodWithDifferenceInVariableName"))
                        check.set(true);
            }
            assertFalse(check.get());
        }






        @Test
        public void newMethodTest() {

            AtomicBoolean check = new AtomicBoolean(false);
            Iterator<String> listIterator = NEW_METHOD_FINDED.iterator();
            while (listIterator.hasNext()) {
                String value = listIterator.next();

                    if (value.equals("sootTest.sootexample.newMethod"))
                        check.set(true);




            }
            assertTrue(check.get());


            check.set(false);


            listIterator = CHANGED_METHOD_FINDED.iterator();
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
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("testNewMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);


        }





        @Test
        public void staticTest() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("testStaticDifferentMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);


        }

        @Test
        public void staticTestForEqualsMethod() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("testStaticEqualMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);


        }



        @Test
        public void constantTest() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("testField".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);


        }



        @Test
        public void finalTest() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("testFinalDifferentMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);


        }

        @Test
        public void finalStaticTest() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("testFinalStaticDifferentMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertTrue(check);


        }

        @Test
        public void finalTestForEqualsMethod() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("testFinalEqualMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);


        }

        @Test
        public void  finalStaticTestForEqualsMethod() {

            boolean check = false;
            for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
                if ("testFinalStaticEqualMethod".equals(t.getTestMethod().getName()))
                    check = true;
            }
            assertFalse(check);


        }

    @Test
    public void testChangedHierarchy(){
        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
            if ("testChangedHierarchy".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertTrue(check);



    }

    @Test
    public void testEqualHierarchy(){
        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
            if ("testEqualHierarchy".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertFalse(check);



    }

    @Test
    public void test1MethodEqualHierarchy(){
        boolean check = false;
        for (String method : CHANGED_METHOD_FINDED) {
            if ("sootTest.FirstClass.fooEqual".equals(method))
                check = true;
        }
        assertFalse(check);
    }

    @Test
    public void test1MethodChangedHierarchy(){
        boolean check = false;
        for (String method : CHANGED_METHOD_FINDED) {
            if ("sootTest.FirstClass.foo".equals(method));
            check = true;
        }
        assertTrue(check);

    }

    @Test
    public void test2MethodEqualHierarchy(){
        boolean check = false;
        for (String method : CHANGED_METHOD_FINDED) {
            if ("sootTest.SecondClass.fooEqual".equals(method))
                check = true;
        }
        assertFalse(check);
    }

    @Test
    public void test2MethodChangedHierarchy(){
        boolean check = false;
        for (String method : CHANGED_METHOD_FINDED) {
            if ("sootTest.ThirdClass.foo".equals(method));
            check = true;
        }
        assertTrue(check);

    }

    @Test
    public void test3MethodEqualHierarchy(){
        boolean check = false;
        for (String method : CHANGED_METHOD_FINDED) {
            if ("sootTest.ThirdClass.fooEqual".equals(method))
                check = true;
        }
        assertFalse(check);
    }
    @Test
    public void test1MethodChangedInClinit(){
        boolean check = false;
        for (String methodString : CHANGED_METHOD_FINDED) {
            if ("sootTest.object.getStaticField".equals(methodString))
                check = true;
        }
        assertTrue(check);



    }

    @Test
    public void test2MethodChangedInClinit(){
        boolean check = false;
        for (String methodString : CHANGED_METHOD_FINDED) {
            if ("sootTest.object.getNormalField".equals(methodString))
                check = true;
        }
        assertTrue(check);

    }


    @Test
    public void testEqualFieldInAClassWithDifferentClinit(){
        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
            if ("testField".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertTrue(check);



    }

    @Test
    public void testDifferentFieldInAClassWithDifferentClinit(){
        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
            if ("testGetStaticField".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertTrue(check);



    }

    @Test
    public void testEqualMethodInAClassWithDifferentClinit(){
        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
            if ("testFoo".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertTrue(check);

    }

    @Test
    public void testChangedTest(){
        boolean check = false;
        for (testSelector.testSelector.Test t : TEST_TO_RUN_FOUND) {
            if ("differentTest".equals(t.getTestMethod().getName()))
                check = true;
        }
        assertTrue(check);

    }

    @Test
    public void testCover2ChangedMethods() {

        for (testSelector.testSelector.Test test : TEST_TO_RUN_FOUND) {
                if ("test2ChangedMethod".equals(test.getTestMethod().getName())){
                    assertTrue(test.getTestingMethods().contains("sootTest.sootexample.secondMethodTested") && test.getTestingMethods().contains("sootTest.sootexample.oneMethodTested"));
                }
        }



    }









}
