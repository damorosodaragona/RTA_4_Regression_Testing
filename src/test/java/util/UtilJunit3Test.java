package util;

import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import soot.SootClass;
import soot.SootMethod;
import testselector.exception.InvalidTargetPaths;
import testselector.exception.NoTestFoundedException;
import testselector.project.Project;
import testselector.util.Util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilJunit3Test {
    private static SootMethod sootMethodMock;
    private static String targetPath;
    static Project p;

    @BeforeAll
    public static void setUp() throws NoSuchMethodException, InvalidTargetPaths, IOException, NoTestFoundedException, IllegalAccessException, InvocationTargetException {
        BasicConfigurator.configure();


        sootMethodMock = mock(SootMethod.class);

        sootMethodMock.setDeclaringClass(new SootClass("sootexampleTestJUnit3"));
        when(sootMethodMock.getDeclaringClass()).thenReturn(new SootClass("sootexampleTestJUnit3"));

        targetPath = "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out\\test\\Junit3Test";

        p = new Project(3, new String[0], targetPath);


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
        Assertions.assertTrue(Util.isSetup(m, 3));
        Assertions.assertTrue(Util.isATestMethod(m, 3));
        Assertions.assertFalse(Util.isJunitTestCase(m, 3));
        Assertions.assertFalse(Util.isTearDown(m, 3));


//        Assertions.assertFalse(Util.isSetup(m, 4));
//        Assertions.assertFalse(Util.isJunitTestCase(m, 4));
//        Assertions.assertFalse(Util.isATestMethod(m, 4));
//        Assertions.assertFalse(Util.isTearDown(m, 4));
//
//        Assertions.assertFalse(Util.isJunitTestCase(m, 5));
//        Assertions.assertFalse(Util.isATestMethod(m, 5));
//        Assertions.assertFalse(Util.isSetup(m, 5));
//        Assertions.assertFalse(Util.isTearDown(m, 5));


    }

    @Test
    public void isTest() {

        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getDeclaringClass().getShortName().equals("sootexampleTestJUnit3"))
                if (method.getName().equals("testFail") || method.getName().equals("testPass")) {
                    m = method;
                    Assertions.assertTrue(Util.isJunitTestCase(m, 3));
                    Assertions.assertTrue(Util.isATestMethod(m, 3));
                    Assertions.assertFalse(Util.isSetup(m, 3));
                    Assertions.assertFalse(Util.isTearDown(m, 3));
/*
                    Assertions.assertFalse(Util.isJunitTestCase(m, 4));
                    Assertions.assertFalse(Util.isATestMethod(m, 4));
                    Assertions.assertFalse(Util.isSetup(m, 4));
                    Assertions.assertFalse(Util.isTearDown(m, 4));

                    Assertions.assertFalse(Util.isSetup(m, 5));
                    Assertions.assertFalse(Util.isTearDown(m, 5));
                    Assertions.assertFalse(Util.isJunitTestCase(m, 5));
                    Assertions.assertFalse(Util.isATestMethod(m, 5));

                    Assertions.assertFalse(Util.isSetup(m, 6));
                    Assertions.assertFalse(Util.isJunitTestCase(m, 6));
                    Assertions.assertFalse(Util.isATestMethod(m, 6));
                    Assertions.assertFalse(Util.isTearDown(m, 6));*/
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
                Assertions.assertFalse(Util.isJunitTestCase(m, 3));
                Assertions.assertFalse(Util.isATestMethod(m, 3));
                Assertions.assertFalse(Util.isSetup(m, 3));
                Assertions.assertFalse(Util.isTearDown(m, 3));

//                Assertions.assertFalse(Util.isJunitTestCase(m, 4));
//                Assertions.assertFalse(Util.isATestMethod(m, 4));
//                Assertions.assertFalse(Util.isSetup(m, 4));
//                Assertions.assertFalse(Util.isTearDown(m, 4));
//
//                Assertions.assertFalse(Util.isJunitTestCase(m, 5));
//                Assertions.assertFalse(Util.isATestMethod(m, 5));
//                Assertions.assertFalse(Util.isSetup(m, 5));
//                Assertions.assertFalse(Util.isTearDown(m, 5));
//
//                Assertions.assertFalse(Util.isSetup(m, 6));
//                Assertions.assertFalse(Util.isJunitTestCase(m, 6));
//                Assertions.assertFalse(Util.isATestMethod(m, 6));
//                Assertions.assertFalse(Util.isTearDown(m, 6));

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

        Assertions.assertFalse(Util.isJunitTestCase(m, 3));
        Assertions.assertTrue(Util.isATestMethod(m, 3));
        Assertions.assertFalse(Util.isSetup(m, 3));
        Assertions.assertTrue(Util.isTearDown(m, 3));

//        Assertions.assertFalse(Util.isJunitTestCase(m, 4));
//        Assertions.assertFalse(Util.isATestMethod(m, 4));
//        Assertions.assertFalse(Util.isSetup(m, 4));
//        Assertions.assertFalse(Util.isTearDown(m, 4));
//
//        Assertions.assertFalse(Util.isJunitTestCase(m, 5));
//        Assertions.assertFalse(Util.isATestMethod(m, 5));
//        Assertions.assertFalse(Util.isSetup(m, 5));
//        Assertions.assertFalse(Util.isTearDown(m, 5));
//
//        Assertions.assertFalse(Util.isSetup(m, 6));
//        Assertions.assertFalse(Util.isJunitTestCase(m, 6));
//        Assertions.assertFalse(Util.isATestMethod(m, 6));
//        Assertions.assertFalse(Util.isTearDown(m, 6));

    }

    @Test
    public void isInNoTestClass() {
        SootMethod m = null;

        for (SootMethod method : p.getApplicationMethod()) {

            if (method.getDeclaringClass().getShortName().equals("noTestClass")) {

                m = method;

                Assertions.assertFalse(Util.isJunitTestCase(m, 3));
                Assertions.assertFalse(Util.isATestMethod(m, 3));
                Assertions.assertFalse(Util.isSetup(m, 3));
                Assertions.assertFalse(Util.isTearDown(m, 3));
/*
                Assertions.assertFalse(Util.isJunitTestCase(m, 4));
                Assertions.assertFalse(Util.isATestMethod(m, 4));
                Assertions.assertFalse(Util.isSetup(m, 4));
                Assertions.assertFalse(Util.isTearDown(m, 4));

                Assertions.assertFalse(Util.isJunitTestCase(m, 5));
                Assertions.assertFalse(Util.isATestMethod(m, 5));
                Assertions.assertFalse(Util.isSetup(m, 5));
                Assertions.assertFalse(Util.isTearDown(m, 5));*/
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
                Assertions.assertTrue(Util.isSetup(m, 3));
                Assertions.assertTrue(Util.isATestMethod(m, 3));
                Assertions.assertFalse(Util.isJunitTestCase(m, 3));
                Assertions.assertFalse(Util.isTearDown(m, 3));


//                Assertions.assertFalse(Util.isSetup(m, 4));
//                Assertions.assertFalse(Util.isJunitTestCase(m, 4));
//                Assertions.assertFalse(Util.isATestMethod(m, 4));
//                Assertions.assertFalse(Util.isTearDown(m, 4));
//
//                Assertions.assertFalse(Util.isJunitTestCase(m, 5));
//                Assertions.assertFalse(Util.isATestMethod(m, 5));
//                Assertions.assertFalse(Util.isSetup(m, 5));
//                Assertions.assertFalse(Util.isTearDown(m, 5));
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
                Assertions.assertFalse(Util.isJunitTestCase(m, 3));
                Assertions.assertTrue(Util.isATestMethod(m, 3));
                Assertions.assertFalse(Util.isSetup(m, 3));
                Assertions.assertTrue(Util.isTearDown(m, 3));

              /*  Assertions.assertFalse(Util.isJunitTestCase(m, 4));
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
                Assertions.assertFalse(Util.isTearDown(m, 6));*/
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
                Assertions.assertTrue(Util.isJunitTestCase(m, 3));
                Assertions.assertTrue(Util.isATestMethod(m, 3));
                Assertions.assertFalse(Util.isSetup(m, 3));
                Assertions.assertFalse(Util.isTearDown(m, 3));

             /*   Assertions.assertFalse(Util.isJunitTestCase(m, 4));
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
                Assertions.assertFalse(Util.isTearDown(m, 6));*/
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

                Assertions.assertFalse(Util.isJunitTestCase(m, 3));
                Assertions.assertFalse(Util.isATestMethod(m, 3));
                Assertions.assertFalse(Util.isSetup(m, 3));
                Assertions.assertFalse(Util.isTearDown(m, 3));

              /*  Assertions.assertFalse(Util.isJunitTestCase(m, 4));
                Assertions.assertFalse(Util.isATestMethod(m, 4));
                Assertions.assertFalse(Util.isSetup(m, 4));
                Assertions.assertFalse(Util.isTearDown(m, 4));

                Assertions.assertFalse(Util.isJunitTestCase(m, 5));
                Assertions.assertFalse(Util.isATestMethod(m, 5));
                Assertions.assertFalse(Util.isSetup(m, 5));
                Assertions.assertFalse(Util.isTearDown(m, 5));*/
            }

        }
        Assertions.assertNotNull(m);




    }



}




