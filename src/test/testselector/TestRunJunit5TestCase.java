//package testselector;
//
//import org.apache.log4j.BasicConfigurator;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import testselector.util.Util;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertAll;
//
//public class TestRunJunit5TestCase {
//    private static Set<testselector.testSelector.Test> realTest;
//    private static testselector.testSelector.Test succeedingStandardTest;
//    private static testselector.testSelector.Test succeedingGroupedTest;
//    private static testselector.testSelector.Test failingTest;
//    private static testselector.testSelector.Test skippedTest;
//    private static testselector.testSelector.Test dependentFailAssertion;
//    private static testselector.testSelector.Test dependentPassAssertion;
//
//    @BeforeClass
//    public static void setUp() {
//        BasicConfigurator.configure();
//        realTest = new HashSet<>();
//        Set<testselector.testSelector.Test> Junit5Test = new HashSet();
//
//        ArrayList<String> path = new ArrayList<>();
//        path.add("out/production/Junit5Test");
//        List<String> cls = new ArrayList<>();
//        cls.add("");
//        testselector.testSelector.Test init = new testselector.testSelector.Test(Util.findMethod("init", "sootexampleTestJUnit5", "test", path, cls));
//        succeedingStandardTest = new testselector.testSelector.Test(Util.findMethod("succeedingStandardTest", "sootexampleTestJUnit5", "test", path, cls));
//        succeedingGroupedTest = new testselector.testSelector.Test(Util.findMethod("succeedingGroupedTest", "sootexampleTestJUnit5", "test", path, cls));
//        failingTest = new testselector.testSelector.Test(Util.findMethod("failingTest", "sootexampleTestJUnit5", "test", path, cls));
//        skippedTest = new testselector.testSelector.Test(Util.findMethod("skippedTest", "sootexampleTestJUnit5", "test", path, cls));
//        testselector.testSelector.Test tearDown = new testselector.testSelector.Test(Util.findMethod("tearDown", "sootexampleTestJUnit5", "test", path, cls));
//        testselector.testSelector.Test tearDownAll = new testselector.testSelector.Test(Util.findMethod("tearDownAll", "sootexampleTestJUnit5", "test", path, cls));
//        dependentFailAssertion = new testselector.testSelector.Test(Util.findMethod("dependentFailAssertion", "sootexampleTestJUnit5", "test", path, cls));
//        dependentPassAssertion = new testselector.testSelector.Test(Util.findMethod("dependentPassAssertion", "sootexampleTestJUnit5", "test", path, cls));
//
//        Junit5Test.add(init);
//        Junit5Test.add(succeedingStandardTest);
//        Junit5Test.add(succeedingGroupedTest);
//        Junit5Test.add(failingTest);
//        Junit5Test.add(skippedTest);
//        Junit5Test.add(tearDown);
//        Junit5Test.add(tearDownAll);
//        Junit5Test.add(dependentFailAssertion);
//        Junit5Test.add(dependentPassAssertion);
//
//        for (testselector.testSelector.Test m : Junit5Test) {
//            if (Util.isJunitTestCase(m.getTestMethod(),5 ))
//                realTest.add(m);
//        }
//    }
//
//
//    @Test
//    public void findJunit5Test() {
//
//        Assert.assertTrue(realTest.contains(succeedingGroupedTest));
//        Assert.assertTrue(realTest.contains(succeedingStandardTest));
//        Assert.assertTrue(realTest.contains(failingTest));
//        Assert.assertTrue(realTest.contains(skippedTest));
//        Assert.assertTrue(realTest.contains(dependentFailAssertion));
//        Assert.assertTrue(realTest.contains(dependentPassAssertion));
//        Assert.assertTrue(realTest.size() == 6);
//    }
//
//
//    @Test
//    public void runJunit5Test() {
//        assertAll("succeedingGroupedTest",
//
//                () -> Assert.assertEquals(1, succeedingGroupedTest.runTest().getTestsSucceededCount()),
//                () -> Assert.assertEquals(0, succeedingGroupedTest.runTest().getTestsFailedCount()),
//                () -> Assert.assertEquals(1, succeedingGroupedTest.runTest().getTotalFailureCount())
//        );
//
//
//        assertAll(
//                () -> Assert.assertEquals(1, succeedingStandardTest.runTest().getTestsSucceededCount()),
//                () -> Assert.assertEquals(0, succeedingStandardTest.runTest().getTestsFailedCount()),
//                () -> Assert.assertEquals(1, succeedingStandardTest.runTest().getTotalFailureCount())
//        );
//
//
//        assertAll(
//                () -> Assert.assertEquals(0, failingTest.runTest().getTestsSucceededCount()),
//                () -> Assert.assertEquals(1, failingTest.runTest().getTestsFailedCount()),
//                () -> Assert.assertEquals(2, failingTest.runTest().getTotalFailureCount())
//        );
//
//        assertAll(
//                () -> Assert.assertEquals(1, skippedTest.runTest().getTestsSkippedCount()),
//                () -> Assert.assertEquals(0, skippedTest.runTest().getTestsFailedCount()),
//                () -> Assert.assertEquals(1, skippedTest.runTest().getTotalFailureCount())
//        );
//
//
//        assertAll(
//                () -> Assert.assertEquals(0, dependentFailAssertion.runTest().getTestsSucceededCount()),
//                () -> Assert.assertEquals(1, dependentFailAssertion.runTest().getTestsFailedCount()),
//                () -> Assert.assertEquals(2, dependentFailAssertion.runTest().getTotalFailureCount())
//        );
//
//        assertAll(
//                () -> Assert.assertEquals(1, dependentPassAssertion.runTest().getTestsSucceededCount()),
//                () -> Assert.assertEquals(0, dependentPassAssertion.runTest().getTestsFailedCount()),
//                () -> Assert.assertEquals(1, dependentPassAssertion.runTest().getTotalFailureCount())
//        );
//
//
//    }
//}
