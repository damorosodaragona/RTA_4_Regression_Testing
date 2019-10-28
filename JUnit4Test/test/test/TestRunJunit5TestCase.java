package test;//package testselector;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import soot.SootClass;
import soot.SootMethod;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRunJunit5TestCase {
    private static Set<testSelector.testSelector.Test> realTest;
    private static testSelector.testSelector.Test succeedingStandardTest;
    private static testSelector.testSelector.Test succeedingGroupedTest;
    private static testSelector.testSelector.Test failingTest;
    private static testSelector.testSelector.Test skippedTest;
    private static testSelector.testSelector.Test dependentFailAssertion;
    private static testSelector.testSelector.Test dependentPassAssertion;

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
        realTest = new HashSet<>();
        Set<testSelector.testSelector.Test> Junit5Test = new HashSet();

        SootMethod succeedingGroupedTestMethod = mock(SootMethod.class);
        SootMethod skippedTestMethod = mock(SootMethod.class);
        SootMethod failingTestMethod = mock(SootMethod.class);
        //    private static Set<testSelector.testSelector.Test> realTest;
        SootMethod succeedingStandardTestMethod = mock(SootMethod.class);
        SootMethod dependentFailAssertionMethod = mock(SootMethod.class);
        SootMethod dependentPassAssertionMethod = mock(SootMethod.class);


        when(succeedingStandardTestMethod.getDeclaringClass()).thenReturn(new SootClass("tsTest.scenaries.ExampleTestJunit5"));
        when(succeedingStandardTestMethod.getName()).thenReturn("succeedingStandardTest");

        when(succeedingGroupedTestMethod.getDeclaringClass()).thenReturn(new SootClass("tsTest.scenaries.ExampleTestJunit5"));
        when(succeedingGroupedTestMethod.getName()).thenReturn("succeedingGroupedTest");

        when(failingTestMethod.getDeclaringClass()).thenReturn(new SootClass("tsTest.scenaries.ExampleTestJunit5"));
        when(failingTestMethod.getName()).thenReturn("failingTest");

        when(skippedTestMethod.getDeclaringClass()).thenReturn(new SootClass("tsTest.scenaries.ExampleTestJunit5"));
        when(skippedTestMethod.getName()).thenReturn("skippedTest");

        when(dependentPassAssertionMethod.getDeclaringClass()).thenReturn(new SootClass("tsTest.scenaries.ExampleTestJunit5"));
        when(dependentPassAssertionMethod.getName()).thenReturn("dependentPassAssertion");

        when(dependentFailAssertionMethod.getDeclaringClass()).thenReturn(new SootClass("tsTest.scenaries.ExampleTestJunit5"));
        when(dependentFailAssertionMethod.getName()).thenReturn("dependentFailAssertion");




        succeedingStandardTest = new testSelector.testSelector.Test(succeedingStandardTestMethod);
        succeedingGroupedTest = new testSelector.testSelector.Test(succeedingGroupedTestMethod);
        failingTest = new testSelector.testSelector.Test(failingTestMethod);
        skippedTest = new testSelector.testSelector.Test(skippedTestMethod);

        dependentFailAssertion = new testSelector.testSelector.Test(dependentFailAssertionMethod);
        dependentPassAssertion = new testSelector.testSelector.Test(dependentPassAssertionMethod);

        Junit5Test.add(succeedingStandardTest);
        Junit5Test.add(succeedingGroupedTest);
        Junit5Test.add(failingTest);
        Junit5Test.add(skippedTest);

        Junit5Test.add(dependentFailAssertion);
        Junit5Test.add(dependentPassAssertion);

    }


    @Test
    public void runJunit5Test() {
        assertAll("succeedingGroupedTest",

                () -> Assert.assertEquals(1, succeedingGroupedTest.runTest().getTestsSucceededCount()),
                () -> Assert.assertEquals(0, succeedingGroupedTest.runTest().getTestsFailedCount()),
                () -> Assert.assertEquals(1, succeedingGroupedTest.runTest().getTotalFailureCount())
        );


        assertAll(
                () -> Assert.assertEquals(1, succeedingStandardTest.runTest().getTestsSucceededCount()),
                () -> Assert.assertEquals(0, succeedingStandardTest.runTest().getTestsFailedCount()),
                () -> Assert.assertEquals(1, succeedingStandardTest.runTest().getTotalFailureCount())
        );


        assertAll(
                () -> Assert.assertEquals(0, failingTest.runTest().getTestsSucceededCount()),
                () -> Assert.assertEquals(1, failingTest.runTest().getTestsFailedCount()),
                () -> Assert.assertEquals(2, failingTest.runTest().getTotalFailureCount())
        );

        assertAll(
                () -> Assert.assertEquals(1, skippedTest.runTest().getTestsSkippedCount()),
                () -> Assert.assertEquals(0, skippedTest.runTest().getTestsFailedCount()),
                () -> Assert.assertEquals(1, skippedTest.runTest().getTotalFailureCount())
        );


        assertAll(
                () -> Assert.assertEquals(0, dependentFailAssertion.runTest().getTestsSucceededCount()),
                () -> Assert.assertEquals(1, dependentFailAssertion.runTest().getTestsFailedCount()),
                () -> Assert.assertEquals(2, dependentFailAssertion.runTest().getTotalFailureCount())
        );

        assertAll(
                () -> Assert.assertEquals(1, dependentPassAssertion.runTest().getTestsSucceededCount()),
                () -> Assert.assertEquals(0, dependentPassAssertion.runTest().getTestsFailedCount()),
                () -> Assert.assertEquals(1, dependentPassAssertion.runTest().getTotalFailureCount())
        );


    }
}
