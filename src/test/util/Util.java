package util;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Util {


    @Test
    public void findJunit3Test() {
        ArrayList<String> path = new ArrayList<>();
        path.add("out/production/Junit3Test");

        Method test1 = testselector.util.Util.findMethod("testFail", "sootexampleTestJUnit3", "test", path);
        Method test2 = testselector.util.Util.findMethod("testPass", "sootexampleTestJUnit3", "test", path);
        Method test3 = testselector.util.Util.findMethod("testNotExistent", "sootexampleTestJUnit3", "test", path);
        Method setUp = testselector.util.Util.findMethod("setUp", "sootexampleTestJUnit3", "test", path);
        Method tearDown = testselector.util.Util.findMethod("tearDown", "sootexampleTestJUnit3", "test", path);

        Assert.assertTrue(testselector.util.Util.isJunitTestCase(test1, ));
        Assert.assertTrue(testselector.util.Util.isJunitTestCase(test2, ));


        Assert.assertEquals(null, test3);
/*
        SootClass sc = new SootClass("out\\production\\Junit3Test");
        SootMethod sm = sc.getMethodByName("setUp");
        Assert.assertTrue(testselector.util.Util.isATestMethod(sm));
*/
    }

    @Test
    public void findJunit5Test() {

        ArrayList<String> path = new ArrayList<>();
        path.add("out/production/Junit5Test");
        Method succeedingStandardTest = testselector.util.Util.findMethod("succeedingStandardTest", "sootexampleTestJUnit5", "test", path);
        Method succeedingGroupedTest = testselector.util.Util.findMethod("succeedingGroupedTest", "sootexampleTestJUnit5", "test", path);
        Method testNotExistent = testselector.util.Util.findMethod("testNotExisting", "sootexampleTestJUnit5", "test", path);

        Assert.assertTrue(testselector.util.Util.isJunitTestCase(succeedingGroupedTest, ));
        Assert.assertTrue(testselector.util.Util.isJunitTestCase(succeedingStandardTest, ));
        Assert.assertEquals(null, testNotExistent);

        Object string = new String();
        testselector.util.Util.isJunitTestCase(string, );
        Assert.assertEquals(false, testselector.util.Util.isJunitTestCase(string, ));
    }


}
