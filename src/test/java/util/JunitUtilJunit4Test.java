package util;

import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import soot.SootMethod;
import CATTO.exception.InvalidTargetPaths;
import CATTO.exception.NoTestFoundedException;
import CATTO.project.Project;
import CATTO.util.JunitUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


//Todo: aggiungere test scritti come per junit3 in progetti junit4 e testare che non vengano riconosciuti

public class JunitUtilJunit4Test {

    private static final String SOOTEXAMPLE_TEST_JUNIT_4 = "sootexampleTestJUnit4";
    private static final String EXTENDED_TEST_CLASS = "extendedTestClass";
    private static String targetPath;
    static Project p;

    @BeforeAll
    public static void setUp() throws NoSuchMethodException, InvalidTargetPaths, IOException, NoTestFoundedException, IllegalAccessException, InvocationTargetException {

        BasicConfigurator.configure();

        targetPath =         targetPath = "whatTestProjectForTesting" + File.separator +  "out" + File.separator + "test" + File.separator + "Junit4Test";

        p = new Project(new String[0], targetPath);


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
        Assertions.assertFalse(JunitUtil.isTearDown(m));
        Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
        Assertions.assertTrue(JunitUtil.isATestMethod(m));
        Assertions.assertTrue(JunitUtil.isSetup(m));
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
        Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
        Assertions.assertTrue(JunitUtil.isATestMethod(m));
        Assertions.assertTrue(JunitUtil.isSetup(m));
        Assertions.assertFalse(JunitUtil.isTearDown(m));

    }

    @Test
    public void isTest() {

        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getDeclaringClass().getShortName().equals(SOOTEXAMPLE_TEST_JUNIT_4))
                if (!method.getName().equals("<init>") && !method.getName().equals("init") && !method.getName().equals("setUp") && !method.getName().equals("tearDown") && !method.getName().equals("tearDownAll") && !method.getName().contains("lambda$") && !method.getName().equals("beforeAll")  && !method.getName().contains("noTest")  && !method.getName().equals("tearDownOverride")  && !method.getName().equals("setUpOverride")  && !method.getName().equals("testOverride")) {

                    m = method;

                    Assertions.assertFalse(JunitUtil.isSetup(m));
                    Assertions.assertFalse(JunitUtil.isTearDown(m));
                    Assertions.assertTrue(JunitUtil.isJunitTestCase(m));
                    Assertions.assertTrue(JunitUtil.isATestMethod(m));

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

                Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
                Assertions.assertFalse(JunitUtil.isATestMethod(m));
                Assertions.assertFalse(JunitUtil.isSetup(m));
                Assertions.assertFalse(JunitUtil.isTearDown(m));
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

                Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
                Assertions.assertFalse(JunitUtil.isATestMethod(m));
                Assertions.assertFalse(JunitUtil.isSetup(m));
                Assertions.assertFalse(JunitUtil.isTearDown(m));
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

        Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
        Assertions.assertTrue(JunitUtil.isATestMethod(m));
        Assertions.assertFalse(JunitUtil.isSetup(m));
        Assertions.assertTrue(JunitUtil.isTearDown(m));

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

        Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
        Assertions.assertTrue(JunitUtil.isATestMethod(m));
        Assertions.assertFalse(JunitUtil.isSetup(m));
        Assertions.assertTrue(JunitUtil.isTearDown(m));

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

        Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
        Assertions.assertTrue(JunitUtil.isATestMethod(m));
        Assertions.assertFalse(JunitUtil.isSetup(m));
        Assertions.assertTrue(JunitUtil.isTearDown(m));

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

        Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
        Assertions.assertTrue(JunitUtil.isATestMethod(m));
        Assertions.assertTrue(JunitUtil.isSetup(m));
        Assertions.assertFalse(JunitUtil.isTearDown(m));

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

        Assertions.assertTrue(JunitUtil.isJunitTestCase(m));
        Assertions.assertTrue(JunitUtil.isATestMethod(m));
        Assertions.assertFalse(JunitUtil.isSetup(m));
        Assertions.assertFalse(JunitUtil.isTearDown(m));
    }

    @Test
    public void isNoTestOverride() {
        SootMethod method = null;
        for (SootMethod m : p.getApplicationMethod()) {
            if (m.getName().equals("noTest1")) {
                method = m;
                Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
                Assertions.assertFalse(JunitUtil.isATestMethod(m));
                Assertions.assertFalse(JunitUtil.isSetup(m));
                Assertions.assertFalse(JunitUtil.isTearDown(m));

            }
        }
        Assertions.assertNotNull(method);


    }





}




