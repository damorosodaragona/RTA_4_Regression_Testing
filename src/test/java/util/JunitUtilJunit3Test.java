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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.io.File;


public class JunitUtilJunit3Test {
    private static String targetPath;
    static Project p;

    @BeforeAll
    public static void setUp() throws NoSuchMethodException, InvalidTargetPaths, IOException, NoTestFoundedException, IllegalAccessException, InvocationTargetException {

        BasicConfigurator.configure();

        targetPath = "whatTestProjectForTesting" + File.separator +  "out" + File.separator + "test" + File.separator + "Junit3Test";

        p = new Project(new String[0], targetPath);


    }


    @Test
    public void isSetup() {

        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("setUp") && method.getDeclaringClass().getShortName().equals("sootexampleTestJUnit3")) {
                m = method;
            }

        }

        Assertions.assertNotNull(m);
        Assertions.assertTrue(JunitUtil.isSetup(m));
        Assertions.assertTrue(JunitUtil.isATestMethod(m));
        Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
        Assertions.assertFalse(JunitUtil.isTearDown(m));
    }

    @Test
    public void isTest() {

        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getDeclaringClass().getShortName().equals("sootexampleTestJUnit3"))
                if (method.getName().equals("testFail") || method.getName().equals("testPass")) {
                    m = method;
                    Assertions.assertTrue(JunitUtil.isJunitTestCase(m));
                    Assertions.assertTrue(JunitUtil.isATestMethod(m));
                    Assertions.assertFalse(JunitUtil.isSetup(m));
                    Assertions.assertFalse(JunitUtil.isTearDown(m));
                }

        }


        Assertions.assertNotNull(m);
    }


    @Test
    public void isNoTest() {
        SootMethod method = null;
        for (SootMethod m : p.getApplicationMethod()) {
            if (m.getName().contains("noTest")) {
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

            if (method.getName().equals("tearDown") && method.getDeclaringClass().getShortName().equals("sootexampleTestJUnit3")) {
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
    public void isInNoTestClass() {
        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getDeclaringClass().getShortName().equals("noTestClass")) {

                m = method;

                Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
                Assertions.assertFalse(JunitUtil.isATestMethod(m));
                Assertions.assertFalse(JunitUtil.isSetup(m));
                Assertions.assertFalse(JunitUtil.isTearDown(m));
            }

        }
        Assertions.assertNotNull(m);




    }

    @Test
    public void isSetupOverride() {

        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("setUp") && method.getDeclaringClass().getShortName().equals("extendedTestClass")) {
                m = method;
                Assertions.assertTrue(JunitUtil.isSetup(m));
                Assertions.assertTrue(JunitUtil.isATestMethod(m));
                Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
                Assertions.assertFalse(JunitUtil.isTearDown(m));
            }

        }

        Assertions.assertNotNull(m);



    }

    @Test
    public void isTearDownOverride() {
        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("tearDown") && method.getDeclaringClass().getShortName().equals("extendedTestClass")) {
                m = method;
                Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
                Assertions.assertTrue(JunitUtil.isATestMethod(m));
                Assertions.assertFalse(JunitUtil.isSetup(m));
                Assertions.assertTrue(JunitUtil.isTearDown(m));
            }

        }
        Assertions.assertNotNull(m);



    }

    @Test
    public void isTestOverride() {
        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getName().equals("test") && method.getDeclaringClass().getShortName().equals("extendedTestClass")) {
                m = method;
                Assertions.assertTrue(JunitUtil.isJunitTestCase(m));
                Assertions.assertTrue(JunitUtil.isATestMethod(m));
                Assertions.assertFalse(JunitUtil.isSetup(m));
                Assertions.assertFalse(JunitUtil.isTearDown(m));

            }

        }
        Assertions.assertNotNull(m);



    }

    @Test
    public void isNoTestParameter() {
        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getDeclaringClass().getShortName().equals("noTest")) {

                m = method;

                Assertions.assertFalse(JunitUtil.isJunitTestCase(m));
                Assertions.assertFalse(JunitUtil.isATestMethod(m));
                Assertions.assertFalse(JunitUtil.isSetup(m));
                Assertions.assertFalse(JunitUtil.isTearDown(m));

            }

        }
        Assertions.assertNotNull(m);




    }



}




