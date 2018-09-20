package com.test;

import com.company.Project;
import com.company.TestSelector;
import com.company.Util;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

public class TestRunJunit5TestCase {
    private static Set<Method> realTest;
    private static Method succeedingStandardTest;
    private static Method init;
    private static Method succeedingGroupedTest;
    private static Method failingTest;
    private static Method skippedTest;
    private static Method tearDown;
    private static Method tearDownAll;
    private static Method dependentFailAssertion;
    private static Method dependentPassAssertion;
    private static String path;

    @BeforeClass
    public static void setUp() {

        realTest = new HashSet<>();
        Set<Method> Junit5Test = new HashSet();
        path = "out/production/Junit5Test";
        init = Util.findMethod("init", "sootexampleTestJUnit5", "test", path);
        succeedingStandardTest = Util.findMethod("succeedingStandardTest", "sootexampleTestJUnit5", "test", path);
        succeedingGroupedTest = Util.findMethod("succeedingGroupedTest", "sootexampleTestJUnit5", "test", path);
        failingTest = Util.findMethod("failingTest", "sootexampleTestJUnit5", "test", path);
        skippedTest = Util.findMethod("skippedTest", "sootexampleTestJUnit5", "test", path);
        tearDown = Util.findMethod("tearDown", "sootexampleTestJUnit5", "test", path);
        tearDownAll = Util.findMethod("tearDownAll", "sootexampleTestJUnit5", "test", path);
        dependentFailAssertion = Util.findMethod("dependentFailAssertion", "sootexampleTestJUnit5", "test", path);
        dependentPassAssertion = Util.findMethod("dependentPassAssertion", "sootexampleTestJUnit5", "test", path);

        Junit5Test.add(init);
        Junit5Test.add(succeedingStandardTest);
        Junit5Test.add(succeedingGroupedTest);
        Junit5Test.add(failingTest);
        Junit5Test.add(skippedTest);
        Junit5Test.add(tearDown);
        Junit5Test.add(tearDownAll);
        Junit5Test.add(dependentFailAssertion);
        Junit5Test.add(dependentPassAssertion);

        for (Method m : Junit5Test) {
            if (Util.isJunitTestCase(m))
                realTest.add(m);
        }
    }


    @Test
    public void findJunit5Test() {

        Assert.assertTrue(realTest.contains(succeedingGroupedTest));
        Assert.assertTrue(realTest.contains(succeedingStandardTest));
        Assert.assertTrue(realTest.contains(failingTest));
        Assert.assertTrue(realTest.contains(skippedTest));
        Assert.assertTrue(realTest.contains(dependentFailAssertion));
        Assert.assertTrue(realTest.contains(dependentPassAssertion));
        Assert.assertTrue(realTest.size() == 6);
    }


    @Test
    public void runJunit5Test() {
        Project p = mock(Project.class);
        Project p1 = mock(Project.class);
        TestSelector t = new TestSelector(p, p1);
        TestSelector tSpy = spy(t);


        doReturn(realTest).when(tSpy).getAllTestToRun();

        when(p1.getPath()).thenReturn(path);

        Set<Method> runned = tSpy.runTestMethods();

        realTest.forEach(test -> Assert.assertTrue(runned.contains(test)));

    }
}
