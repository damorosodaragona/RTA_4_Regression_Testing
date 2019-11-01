package test;

import junitrunner.Runner;
import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import soot.SootClass;
import soot.SootMethod;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRunJunit5TestCase {
    private static Set<testselector.testselector.Test> realTest;
    private static testselector.testselector.Test succeedingStandardTest;
    private static testselector.testselector.Test succeedingGroupedTest;
    private static testselector.testselector.Test failingTest;
    private static testselector.testselector.Test skippedTest;
    private static testselector.testselector.Test dependentFailAssertion;
    private static testselector.testselector.Test dependentPassAssertion;
    private static ArrayList<String> targetPath;
    private static SootClass sootTestClass;

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
        realTest = new HashSet<>();
        Set<testselector.testselector.Test> Junit5Test = new HashSet();

        targetPath = new ArrayList<String>();
        targetPath.add("C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out\\test\\Junit5Test\\test");

        sootTestClass = new SootClass("sootexampleTestJUnit5");

        SootMethod succeedingGroupedTestMethod = mock(SootMethod.class);
        SootMethod skippedTestMethod = mock(SootMethod.class);
        SootMethod failingTestMethod = mock(SootMethod.class);
        SootMethod succeedingStandardTestMethod = mock(SootMethod.class);
        SootMethod dependentFailAssertionMethod = mock(SootMethod.class);
        SootMethod dependentPassAssertionMethod = mock(SootMethod.class);


        when(succeedingStandardTestMethod.getDeclaringClass()).thenReturn(sootTestClass);
        when(succeedingStandardTestMethod.getName()).thenReturn("succeedingStandardTest");

        when(succeedingGroupedTestMethod.getDeclaringClass()).thenReturn(sootTestClass);
        when(succeedingGroupedTestMethod.getName()).thenReturn("succeedingGroupedTest");

        when(failingTestMethod.getDeclaringClass()).thenReturn(sootTestClass);
        when(failingTestMethod.getName()).thenReturn("failingTest");

        when(skippedTestMethod.getDeclaringClass()).thenReturn(sootTestClass);
        when(skippedTestMethod.getName()).thenReturn("skippedTest");

        when(dependentPassAssertionMethod.getDeclaringClass()).thenReturn(sootTestClass);
        when(dependentPassAssertionMethod.getName()).thenReturn("dependentPassAssertion");

        when(dependentFailAssertionMethod.getDeclaringClass()).thenReturn(sootTestClass);
        when(dependentFailAssertionMethod.getName()).thenReturn("dependentFailAssertion");


        succeedingStandardTest = new testselector.testselector.Test(succeedingStandardTestMethod);
        succeedingGroupedTest = new testselector.testselector.Test(succeedingGroupedTestMethod);
        failingTest = new testselector.testselector.Test(failingTestMethod);
        skippedTest = new testselector.testselector.Test(skippedTestMethod);

        dependentFailAssertion = new testselector.testselector.Test(dependentFailAssertionMethod);
        dependentPassAssertion = new testselector.testselector.Test(dependentPassAssertionMethod);

        Junit5Test.add(succeedingStandardTest);
        Junit5Test.add(succeedingGroupedTest);
        Junit5Test.add(failingTest);
        Junit5Test.add(skippedTest);

        Junit5Test.add(dependentFailAssertion);
        Junit5Test.add(dependentPassAssertion);

    }


    @Test
    public void runJunit5Test() throws NoSuchMethodException, IOException, IllegalAccessException, InvocationTargetException {


        Assert.assertEquals(1, Runner.run(succeedingGroupedTest, new String[0], targetPath).getTestsSucceededCount());
        Assert.assertEquals(0, Runner.run(succeedingGroupedTest, new String[0], targetPath).getTestsFailedCount());
        Assert.assertEquals(1, Runner.run(succeedingGroupedTest, new String[0], targetPath).getTotalFailureCount());


        Assert.assertEquals(1, Runner.run(succeedingStandardTest, new String[0], targetPath).getTestsSucceededCount());
        Assert.assertEquals(0, Runner.run(succeedingStandardTest, new String[0], targetPath).getTestsFailedCount());
        Assert.assertEquals(1, Runner.run(succeedingStandardTest, new String[0], targetPath).getTotalFailureCount());


        Assert.assertEquals(0, Runner.run(failingTest, new String[0], targetPath).getTestsSucceededCount());   Assert.assertEquals(1, Runner.run(failingTest, new String[0], targetPath).getTestsFailedCount());
        Assert.assertEquals(2, Runner.run(failingTest, new String[0], targetPath).getTotalFailureCount());


        Assert.assertEquals(1, Runner.run(skippedTest, new String[0], targetPath).getTestsSkippedCount());
        Assert.assertEquals(0, Runner.run(skippedTest, new String[0], targetPath).getTestsFailedCount());
        Assert.assertEquals(1, Runner.run(skippedTest, new String[0], targetPath).getTotalFailureCount());

        Assert.assertEquals(0, Runner.run(dependentFailAssertion, new String[0], targetPath).getTestsSucceededCount());
        Assert.assertEquals(1, Runner.run(dependentFailAssertion, new String[0], targetPath).getTestsFailedCount());
        Assert.assertEquals(2, Runner.run(dependentFailAssertion, new String[0], targetPath).getTotalFailureCount());

        Assert.assertEquals(1, Runner.run(dependentPassAssertion, new String[0], targetPath).getTestsSucceededCount());
        Assert.assertEquals(0, Runner.run(dependentPassAssertion, new String[0], targetPath).getTestsFailedCount());
        Assert.assertEquals(1, Runner.run(dependentPassAssertion, new String[0], targetPath).getTotalFailureCount());


    }
}
