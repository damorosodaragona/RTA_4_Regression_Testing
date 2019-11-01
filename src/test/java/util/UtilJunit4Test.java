package util;

import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import soot.SootMethod;
import testselector.exception.InvalidTargetPaths;
import testselector.exception.NoTestFoundedException;
import testselector.project.Project;
import testselector.util.Util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class UtilJunit4Test {
    public static final String SOOTEXAMPLE_TEST_JUNIT_4 = "sootexampleTestJUnit4";
    public static final String EXTENDED_TEST_CLASS = "extendedTestClass";
    private static SootMethod sootMethodMock;
    private static String targetPath;
    static Project p;

    @BeforeAll
    public static void setUp() throws NoSuchMethodException, InvalidTargetPaths, IOException, NoTestFoundedException, IllegalAccessException, InvocationTargetException {

        BasicConfigurator.configure();

        targetPath = "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out\\test\\Junit4Test";

        p = new Project(5, new String[0], targetPath);


    }


    @Test
    public void isSetup() {

        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("setUp") && method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_4)) {
                m = method;
            }


        }

        Assertions.assertNotNull(m);
        Assertions.assertFalse(Util.isSetup(m, 3));
        Assertions.assertFalse(Util.isATestMethod(m, 3));
        Assertions.assertFalse(Util.isJunitTestCase(m, 3));
        Assertions.assertFalse(Util.isTearDown(m, 3));


        Assertions.assertFalse(Util.isSetup(m, 5));
        Assertions.assertTrue(Util.isATestMethod(m, 4));
        Assertions.assertTrue(Util.isSetup(m, 4));
        Assertions.assertFalse(Util.isATestMethod(m, 5));
        Assertions.assertFalse(Util.isTearDown(m, 4));
        Assertions.assertFalse(Util.isJunitTestCase(m, 4));


        Assertions.assertFalse(Util.isJunitTestCase(m, 5));

        Assertions.assertFalse(Util.isTearDown(m, 5));


        Assertions.assertFalse(Util.isSetup(m, 6));
        Assertions.assertFalse(Util.isJunitTestCase(m, 6));
        Assertions.assertFalse(Util.isATestMethod(m, 6));
        Assertions.assertFalse(Util.isTearDown(m, 6));


    }

    @Test
    public void isBeforeAll() {

        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("beforeAll") && method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_4)) {
                m = method;
            }

        }

        Assertions.assertNotNull(m);
        Assertions.assertFalse(Util.isSetup(m, 3));
        Assertions.assertFalse(Util.isATestMethod(m, 3));
        Assertions.assertFalse(Util.isJunitTestCase(m, 3));
        Assertions.assertFalse(Util.isTearDown(m, 3));


        Assertions.assertFalse(Util.isSetup(m, 5));
        Assertions.assertFalse(Util.isJunitTestCase(m, 4));
        Assertions.assertFalse(Util.isATestMethod(m, 5));
        Assertions.assertFalse(Util.isTearDown(m, 4));

        Assertions.assertFalse(Util.isJunitTestCase(m, 5));
        Assertions.assertTrue(Util.isATestMethod(m, 4));
        Assertions.assertTrue(Util.isSetup(m, 4));
        Assertions.assertFalse(Util.isTearDown(m, 5));

        Assertions.assertFalse(Util.isSetup(m, 6));
        Assertions.assertFalse(Util.isJunitTestCase(m, 6));
        Assertions.assertFalse(Util.isATestMethod(m, 6));
        Assertions.assertFalse(Util.isTearDown(m, 6));


    }

    @Test
    public void isTest() {

        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_4))
                if (!method.getName().equals("<init>") && !method.getName().equals("init") && !method.getName().equals("setUp") && !method.getName().equals("tearDown") && !method.getName().equals("tearDownAll") && !method.getName().contains("lambda$") && !method.getName().equals("beforeAll")  && !method.getName().contains("noTest")  && !method.getName().equals("tearDownOverride")  && !method.getName().equals("setUpOverride")  && !method.getName().equals("testOverride")) {
                    m = method;

                    Assertions.assertFalse(Util.isJunitTestCase(m, 3));
                    Assertions.assertFalse(Util.isATestMethod(m, 3));
                    Assertions.assertFalse(Util.isSetup(m, 3));
                    Assertions.assertFalse(Util.isTearDown(m, 3));

                    //todo: un metodo junit4 viene riconosciuto come junit5
                  //  Assertions.assertFalse(Util.isJunitTestCase(m, 5));
                  //  Assertions.assertFalse(Util.isATestMethod(m, 5));
                    Assertions.assertFalse(Util.isSetup(m, 4));
                    Assertions.assertFalse(Util.isTearDown(m, 4));

                    Assertions.assertFalse(Util.isSetup(m, 5));
                    Assertions.assertFalse(Util.isTearDown(m, 5));
                    Assertions.assertTrue(Util.isJunitTestCase(m, 4));
                    Assertions.assertTrue(Util.isATestMethod(m, 4));

                    Assertions.assertFalse(Util.isSetup(m, 6));
                    Assertions.assertFalse(Util.isJunitTestCase(m, 6));
                    Assertions.assertFalse(Util.isATestMethod(m, 6));
                    Assertions.assertFalse(Util.isTearDown(m, 6));
                }

        }


        Assertions.assertNotNull(m);
    }


    @Test
    public void isNoTest() {
        SootMethod method = null;
        for (SootMethod m : p.getApplicationMethod()) {
            if (m.getName().equals("noTest")) {
                method = m;
                Assertions.assertFalse(Util.isJunitTestCase(m, 3));
                Assertions.assertFalse(Util.isATestMethod(m, 3));
                Assertions.assertFalse(Util.isSetup(m, 3));
                Assertions.assertFalse(Util.isTearDown(m, 3));

                Assertions.assertFalse(Util.isJunitTestCase(m, 4));
                Assertions.assertFalse(Util.isATestMethod(m, 4));
                Assertions.assertFalse(Util.isSetup(m, 4));
                Assertions.assertFalse(Util.isTearDown(m, 4));

                Assertions.assertFalse(Util.isJunitTestCase(m, 5));
                Assertions.assertFalse(Util.isATestMethod(m, 5));
                Assertions.assertFalse(Util.isSetup(m, 5));
                Assertions.assertFalse(Util.isTearDown(m, 5));

                Assertions.assertFalse(Util.isSetup(m, 6));
                Assertions.assertFalse(Util.isJunitTestCase(m, 6));
                Assertions.assertFalse(Util.isATestMethod(m, 6));
                Assertions.assertFalse(Util.isTearDown(m, 6));

            }
        }
        Assertions.assertNotNull(method);


    }

    @Test
    public void isTearDown() {
        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("tearDown") && method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_4)) {
                m = method;
            }

        }
        Assertions.assertNotNull(m);

        Assertions.assertFalse(Util.isJunitTestCase(m, 3));
        Assertions.assertFalse(Util.isATestMethod(m, 3));
        Assertions.assertFalse(Util.isSetup(m, 3));
        Assertions.assertFalse(Util.isTearDown(m, 3));

        Assertions.assertFalse(Util.isJunitTestCase(m, 4));
        Assertions.assertFalse(Util.isATestMethod(m, 5));
        Assertions.assertFalse(Util.isSetup(m, 4));
        Assertions.assertFalse(Util.isTearDown(m, 5));

        Assertions.assertFalse(Util.isJunitTestCase(m, 5));
        Assertions.assertTrue(Util.isATestMethod(m, 4));
        Assertions.assertFalse(Util.isSetup(m, 5));
        Assertions.assertTrue(Util.isTearDown(m, 4));

        Assertions.assertFalse(Util.isSetup(m, 6));
        Assertions.assertFalse(Util.isJunitTestCase(m, 6));
        Assertions.assertFalse(Util.isATestMethod(m, 6));
        Assertions.assertFalse(Util.isTearDown(m, 6));

    }

    @Test
    public void isTearDownAll() {
        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("tearDownAll") && method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_4)) {
                m = method;
            }

        }
        Assertions.assertNotNull(m);

        Assertions.assertFalse(Util.isJunitTestCase(m, 3));
        Assertions.assertFalse(Util.isATestMethod(m, 3));
        Assertions.assertFalse(Util.isSetup(m, 3));
        Assertions.assertFalse(Util.isTearDown(m, 3));

        Assertions.assertFalse(Util.isJunitTestCase(m, 4));
        Assertions.assertFalse(Util.isATestMethod(m, 5));
        Assertions.assertFalse(Util.isSetup(m, 4));
        Assertions.assertFalse(Util.isTearDown(m, 5));

        Assertions.assertFalse(Util.isJunitTestCase(m, 5));
        Assertions.assertTrue(Util.isATestMethod(m, 4));
        Assertions.assertFalse(Util.isSetup(m, 5));
        Assertions.assertTrue(Util.isTearDown(m, 4));

        Assertions.assertFalse(Util.isSetup(m, 6));
        Assertions.assertFalse(Util.isJunitTestCase(m, 6));
        Assertions.assertFalse(Util.isATestMethod(m, 6));
        Assertions.assertFalse(Util.isTearDown(m, 6));

    }


    @Test
    public void isTearDownOverride() {
        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("tearDownOverride") && (method.getDeclaringClass().getShortName().equals(EXTENDED_TEST_CLASS))) {
                m = method;
            }


        }
        Assertions.assertNotNull(m);

        Assertions.assertFalse(Util.isJunitTestCase(m, 3));
        Assertions.assertFalse(Util.isATestMethod(m, 3));
        Assertions.assertFalse(Util.isSetup(m, 3));
        Assertions.assertFalse(Util.isTearDown(m, 3));

        Assertions.assertFalse(Util.isJunitTestCase(m, 4));
        Assertions.assertFalse(Util.isATestMethod(m, 5));
        Assertions.assertFalse(Util.isSetup(m, 4));
        Assertions.assertFalse(Util.isTearDown(m, 5));

        Assertions.assertFalse(Util.isJunitTestCase(m, 5));
        Assertions.assertTrue(Util.isATestMethod(m, 4));
        Assertions.assertFalse(Util.isSetup(m, 5));
        Assertions.assertTrue(Util.isTearDown(m, 4));

        Assertions.assertFalse(Util.isSetup(m, 6));
        Assertions.assertFalse(Util.isJunitTestCase(m, 6));
        Assertions.assertFalse(Util.isATestMethod(m, 6));
        Assertions.assertFalse(Util.isTearDown(m, 6));

    }

    @Test
    public void isSetupOverride() {

        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("setUpOverride") && method.getDeclaringClass().getShortName().equals(EXTENDED_TEST_CLASS)) {
                m = method;
            }

        }

        Assertions.assertNotNull(m);
        Assertions.assertFalse(Util.isSetup(m, 3));
        Assertions.assertFalse(Util.isATestMethod(m, 3));
        Assertions.assertFalse(Util.isJunitTestCase(m, 3));
        Assertions.assertFalse(Util.isTearDown(m, 3));


        Assertions.assertFalse(Util.isSetup(m, 5));
        Assertions.assertFalse(Util.isJunitTestCase(m, 4));
        Assertions.assertFalse(Util.isATestMethod(m, 5));
        Assertions.assertFalse(Util.isTearDown(m, 4));

        Assertions.assertFalse(Util.isJunitTestCase(m, 5));
        Assertions.assertTrue(Util.isATestMethod(m, 4));
        Assertions.assertTrue(Util.isSetup(m, 4));
        Assertions.assertFalse(Util.isTearDown(m, 5));


        Assertions.assertFalse(Util.isSetup(m, 6));
        Assertions.assertFalse(Util.isJunitTestCase(m, 6));
        Assertions.assertFalse(Util.isATestMethod(m, 6));
        Assertions.assertFalse(Util.isTearDown(m, 6));


    }

    @Test
    public void isTestOverride() {
        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("testOverride") && method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_4)) {
                m = method;
            }

        }
        Assertions.assertNotNull(m);

        Assertions.assertFalse(Util.isJunitTestCase(m, 3));
        Assertions.assertFalse(Util.isATestMethod(m, 3));
        Assertions.assertFalse(Util.isSetup(m, 3));
        Assertions.assertFalse(Util.isTearDown(m, 3));


        //todo: un metodo junit4 viene riconosciuto come junit5
       // Assertions.assertFalse(Util.isJunitTestCase(m, 5));
      //  Assertions.assertFalse(Util.isATestMethod(m, 5));
        Assertions.assertFalse(Util.isSetup(m, 4));
        Assertions.assertFalse(Util.isTearDown(m, 4));

        Assertions.assertTrue(Util.isJunitTestCase(m, 4));
        Assertions.assertTrue(Util.isATestMethod(m, 4));
        Assertions.assertFalse(Util.isSetup(m, 5));
        Assertions.assertFalse(Util.isTearDown(m, 5));

        Assertions.assertFalse(Util.isSetup(m, 6));
        Assertions.assertFalse(Util.isJunitTestCase(m, 6));
        Assertions.assertFalse(Util.isATestMethod(m, 6));
        Assertions.assertFalse(Util.isTearDown(m, 6));

    }

    @Test
    public void isNoTestOverride() {
        SootMethod method = null;
        for (SootMethod m : p.getApplicationMethod()) {
            if (m.getName().equals("noTest1")) {
                method = m;
                Assertions.assertFalse(Util.isJunitTestCase(m, 3));
                Assertions.assertFalse(Util.isATestMethod(m, 3));
                Assertions.assertFalse(Util.isSetup(m, 3));
                Assertions.assertFalse(Util.isTearDown(m, 3));

                Assertions.assertFalse(Util.isJunitTestCase(m, 4));
                Assertions.assertFalse(Util.isATestMethod(m, 4));
                Assertions.assertFalse(Util.isSetup(m, 4));
                Assertions.assertFalse(Util.isTearDown(m, 4));

                Assertions.assertFalse(Util.isJunitTestCase(m, 5));
                Assertions.assertFalse(Util.isATestMethod(m, 5));
                Assertions.assertFalse(Util.isSetup(m, 5));
                Assertions.assertFalse(Util.isTearDown(m, 5));

                Assertions.assertFalse(Util.isSetup(m, 6));
                Assertions.assertFalse(Util.isJunitTestCase(m, 6));
                Assertions.assertFalse(Util.isATestMethod(m, 6));
                Assertions.assertFalse(Util.isTearDown(m, 6));

            }
        }
        Assertions.assertNotNull(method);


    }




}




