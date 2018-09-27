package test.testSelector;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import testSelector.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;

public class TestRunJunit5TestCase {
    private static Set<testSelector.testSelector.Test> realTest;
    private static testSelector.testSelector.Test succeedingStandardTest;
    private static testSelector.testSelector.Test init;
    private static testSelector.testSelector.Test succeedingGroupedTest;
    private static testSelector.testSelector.Test failingTest;
    private static testSelector.testSelector.Test skippedTest;
    private static testSelector.testSelector.Test tearDown;
    private static testSelector.testSelector.Test tearDownAll;
    private static testSelector.testSelector.Test dependentFailAssertion;
    private static testSelector.testSelector.Test dependentPassAssertion;
    private static ArrayList<String> path;

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
        realTest = new HashSet<testSelector.testSelector.Test>();
        Set<testSelector.testSelector.Test> Junit5Test = new HashSet();

        path = new ArrayList<>();
        path.add("out/production/Junit5Test");

        init = new testSelector.testSelector.Test(Util.findMethod("init", "sootexampleTestJUnit5", "test", path));
        succeedingStandardTest = new testSelector.testSelector.Test(Util.findMethod("succeedingStandardTest", "sootexampleTestJUnit5", "test", path));
        succeedingGroupedTest = new testSelector.testSelector.Test(Util.findMethod("succeedingGroupedTest", "sootexampleTestJUnit5", "test", path));
        failingTest = new testSelector.testSelector.Test(Util.findMethod("failingTest", "sootexampleTestJUnit5", "test", path));
        skippedTest = new testSelector.testSelector.Test(Util.findMethod("skippedTest", "sootexampleTestJUnit5", "test", path));
        tearDown = new testSelector.testSelector.Test(Util.findMethod("tearDown", "sootexampleTestJUnit5", "test", path));
        tearDownAll = new testSelector.testSelector.Test(Util.findMethod("tearDownAll", "sootexampleTestJUnit5", "test", path));
        dependentFailAssertion = new testSelector.testSelector.Test(Util.findMethod("dependentFailAssertion", "sootexampleTestJUnit5", "test", path));
        dependentPassAssertion = new testSelector.testSelector.Test(Util.findMethod("dependentPassAssertion", "sootexampleTestJUnit5", "test", path));

        Junit5Test.add(init);
        Junit5Test.add(succeedingStandardTest);
        Junit5Test.add(succeedingGroupedTest);
        Junit5Test.add(failingTest);
        Junit5Test.add(skippedTest);
        Junit5Test.add(tearDown);
        Junit5Test.add(tearDownAll);
        Junit5Test.add(dependentFailAssertion);
        Junit5Test.add(dependentPassAssertion);

        for (testSelector.testSelector.Test m : Junit5Test) {
            if (Util.isJunitTestCase(m.getTestMethod()))
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
