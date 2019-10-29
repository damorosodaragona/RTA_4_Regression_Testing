package JUnitRunner;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import soot.SootClass;
import soot.SootMethod;

import java.util.ArrayList;
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
    private static ArrayList<String> targetPath;
    private static SootClass sootTestClass;

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
        realTest = new HashSet<>();
        Set<testSelector.testSelector.Test> Junit5Test = new HashSet();

        targetPath = new ArrayList<String>();
        targetPath.add("C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out\\test\\Junit5Test\\test");

        sootTestClass = new SootClass("sootexampleTestJUnit5");

        SootMethod succeedingGroupedTestMethod = mock(SootMethod.class);
        SootMethod skippedTestMethod = mock(SootMethod.class);
        SootMethod failingTestMethod = mock(SootMethod.class);
        //    private static Set<testSelector.testSelector.Test> realTest;
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

                () -> Assert.assertEquals(1, Runner.run(succeedingGroupedTest, new String[0], targetPath).getTestsSucceededCount()),
                () -> Assert.assertEquals(0, Runner.run(succeedingGroupedTest, new String[0], targetPath).getTestsFailedCount()),
                () -> Assert.assertEquals(1, Runner.run(succeedingGroupedTest, new String[0], targetPath).getTotalFailureCount())
        );


        assertAll(
                () -> Assert.assertEquals(1, Runner.run(succeedingStandardTest, new String[0], targetPath).getTestsSucceededCount()),
                () -> Assert.assertEquals(0, Runner.run(succeedingStandardTest, new String[0], targetPath).getTestsFailedCount()),
                () -> Assert.assertEquals(1, Runner.run(succeedingStandardTest, new String[0], targetPath).getTotalFailureCount())
        );


        assertAll(
                () -> Assert.assertEquals(0, Runner.run(failingTest, new String[0], targetPath).getTestsSucceededCount()),
                () -> Assert.assertEquals(1, Runner.run(failingTest, new String[0], targetPath).getTestsFailedCount()),
                () -> Assert.assertEquals(2, Runner.run(failingTest, new String[0], targetPath).getTotalFailureCount())
        );

        assertAll(
                () -> Assert.assertEquals(1, Runner.run(skippedTest, new String[0], targetPath).getTestsSkippedCount()),
                () -> Assert.assertEquals(0, Runner.run(skippedTest, new String[0], targetPath).getTestsFailedCount()),
                () -> Assert.assertEquals(1, Runner.run(skippedTest, new String[0], targetPath).getTotalFailureCount())
        );


        assertAll(
                () -> Assert.assertEquals(0, Runner.run(dependentFailAssertion, new String[0], targetPath).getTestsSucceededCount()),
                () -> Assert.assertEquals(1, Runner.run(dependentFailAssertion, new String[0], targetPath).getTestsFailedCount()),
                () -> Assert.assertEquals(2, Runner.run(dependentFailAssertion, new String[0], targetPath).getTotalFailureCount())
        );

        assertAll(
                () -> Assert.assertEquals(1, Runner.run(dependentPassAssertion, new String[0], targetPath).getTestsSucceededCount()),
                () -> Assert.assertEquals(0,Runner.run(dependentPassAssertion, new String[0], targetPath).getTestsFailedCount()),
                () -> Assert.assertEquals(1, Runner.run(dependentPassAssertion, new String[0], targetPath).getTotalFailureCount())
        );


    }
}
