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

public class UtilJunit5Test {
    private static final String SOOTEXAMPLE_TEST_JUNIT_5 = "sootexampleTestJUnit5";
    private static final String EXTENDED_TEST_CLASS = "extendedTestClass";

    private static String targetPath;
    static Project p;

    //Todo: aggiungere test scritti come per junit3 in progetti junit5 e testare che non vengano riconosciuti

    @BeforeAll
    public static void setUp() throws NoSuchMethodException, InvalidTargetPaths, IOException, NoTestFoundedException, IllegalAccessException, InvocationTargetException {

        BasicConfigurator.configure();

        targetPath = "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out\\test\\Junit5Test";

        p = new Project(5, new String[0], targetPath);


    }


    @Test
    public void isSetup() {

        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("setUp") && method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_5)) {
                m = method;
            }

        }

        Assertions.assertNotNull(m);

        Assertions.assertFalse(Util.isJunitTestCase(m));
        Assertions.assertTrue(Util.isATestMethod(m));
        Assertions.assertTrue(Util.isSetup(m));
        Assertions.assertFalse(Util.isTearDown(m));

    }

    @Test
    public void isBeforeAll() {

        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("beforeAll") && method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_5)) {
                m = method;
            }

        }

        Assertions.assertNotNull(m);

        Assertions.assertFalse(Util.isJunitTestCase(m));
        Assertions.assertTrue(Util.isATestMethod(m));
        Assertions.assertTrue(Util.isSetup(m));
        Assertions.assertFalse(Util.isTearDown(m));

    }

    @Test
    public void isTest() {

        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_5))
                if (!method.getName().equals("<init>") && !method.getName().equals("init") && !method.getName().equals("setUp") && !method.getName().equals("tearDown") && !method.getName().equals("tearDownAll") && !method.getName().contains("lambda$") && !method.getName().equals("beforeAll")  && !method.getName().contains("noTest")  && !method.getName().equals("tearDownOverride")  && !method.getName().equals("setUpOverride")  && !method.getName().equals("testOverride")) {
                    m = method;

                    Assertions.assertFalse(Util.isSetup(m));
                    Assertions.assertFalse(Util.isTearDown(m));
                    Assertions.assertTrue(Util.isJunitTestCase(m));
                    Assertions.assertTrue(Util.isATestMethod(m));

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

                Assertions.assertFalse(Util.isJunitTestCase(m));
                Assertions.assertFalse(Util.isATestMethod(m));
                Assertions.assertFalse(Util.isSetup(m));
                Assertions.assertFalse(Util.isTearDown(m));


            }
        }
        Assertions.assertNotNull(method);


    }

    @Test
    public void isNoTest2() {
        SootMethod method = null;
        for (SootMethod m : p.getApplicationMethod()) {
            if ( m.getDeclaringClass().getShortName().equals("noTest")) {
                method = m;
                Assertions.assertFalse(Util.isJunitTestCase(m));
                Assertions.assertFalse(Util.isATestMethod(m));
                Assertions.assertFalse(Util.isSetup(m));
                Assertions.assertFalse(Util.isTearDown(m));
            }
        }
        Assertions.assertNotNull(method);


    }

    @Test
    public void isTearDown() {
        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("tearDown") && method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_5)) {
                m = method;
            }

        }
        Assertions.assertNotNull(m);

        Assertions.assertFalse(Util.isJunitTestCase(m));
        Assertions.assertTrue(Util.isATestMethod(m));
        Assertions.assertFalse(Util.isSetup(m));
        Assertions.assertTrue(Util.isTearDown(m));

    }

    @Test
    public void isTearDownAll() {
        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("tearDownAll") && method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_5)) {
                m = method;
            }

        }
        Assertions.assertNotNull(m);

        Assertions.assertFalse(Util.isJunitTestCase(m));
        Assertions.assertTrue(Util.isATestMethod(m));
        Assertions.assertFalse(Util.isSetup(m));
        Assertions.assertTrue(Util.isTearDown(m));

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

        Assertions.assertFalse(Util.isJunitTestCase(m));
        Assertions.assertTrue(Util.isATestMethod(m));
        Assertions.assertFalse(Util.isSetup(m));
        Assertions.assertTrue(Util.isTearDown(m));
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

        Assertions.assertFalse(Util.isJunitTestCase(m));
        Assertions.assertTrue(Util.isATestMethod(m));
        Assertions.assertTrue(Util.isSetup(m));
        Assertions.assertFalse(Util.isTearDown(m));

    }

    @Test
    public void isTestOverride() {
        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("testOverride") && method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_5)) {
                m = method;
            }

        }
        Assertions.assertNotNull(m);

        Assertions.assertTrue(Util.isJunitTestCase(m));
        Assertions.assertTrue(Util.isATestMethod(m));
        Assertions.assertFalse(Util.isSetup(m));
        Assertions.assertFalse(Util.isTearDown(m));
    }

    @Test
    public void isNoTestOverride() {
        SootMethod method = null;
        for (SootMethod m : p.getApplicationMethod()) {
            if (m.getName().equals("noTest1")) {
                method = m;

                Assertions.assertFalse(Util.isJunitTestCase(m));
                Assertions.assertFalse(Util.isATestMethod(m));
                Assertions.assertFalse(Util.isSetup(m));
                Assertions.assertFalse(Util.isTearDown(m));

            }
        }
        Assertions.assertNotNull(method);


    }




}




