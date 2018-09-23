package test.util;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Util {


    @Test
    public void findJunit3Test() {
        ArrayList<String> path = new ArrayList<>();
        path.add("out/production/Junit3Test");

        Method test1 = testSelector.util.Util.findMethod("testFail", "sootexampleTestJUnit3", "test", path);
        Method test2 = testSelector.util.Util.findMethod("testPass", "sootexampleTestJUnit3", "test", path);
        Method test3 = testSelector.util.Util.findMethod("testNotExistent", "sootexampleTestJUnit3", "test", path);

        Assert.assertTrue(testSelector.util.Util.isJunitTestCase(test1));
        Assert.assertTrue(testSelector.util.Util.isJunitTestCase(test2));
        Assert.assertEquals(null, test3);

    }

    @Test
    public void findJunit5Test() {

        ArrayList<String> path = new ArrayList<>();
        path.add("out/production/Junit5Test");
        Method succeedingStandardTest = testSelector.util.Util.findMethod("succeedingStandardTest", "sootexampleTestJUnit5", "test", path);
        Method succeedingGroupedTest = testSelector.util.Util.findMethod("succeedingGroupedTest", "sootexampleTestJUnit5", "test", path);
        Method testNotExistent = testSelector.util.Util.findMethod("testNotExisting", "sootexampleTestJUnit5", "test", path);

        Assert.assertTrue(testSelector.util.Util.isJunitTestCase(succeedingGroupedTest));
        Assert.assertTrue(testSelector.util.Util.isJunitTestCase(succeedingStandardTest));
        Assert.assertEquals(null, testNotExistent);

        Object string = new String();
        testSelector.util.Util.isJunitTestCase(string);
        Assert.assertEquals(false, testSelector.util.Util.isJunitTestCase(string));
    }


}
