package util;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Util {


    @Test
    public void findJunit3Test() {
        ArrayList<String> target = new ArrayList<>();
        target.add("out/production/Junit3Test");
        ArrayList<String> classPath = new ArrayList<>();
        classPath.add("");
        Method test1 = testselector.util.Util.findMethod("testFail", "sootexampleTestJUnit3", "test", target, classPath);
       Method test2 = testselector.util.Util.findMethod("testPass", "sootexampleTestJUnit3", "test", target, classPath);
       Method test3 = testselector.util.Util.findMethod("testNotExistent", "sootexampleTestJUnit3", "test", target, classPath);
       Method setUp = testselector.util.Util.findMethod("setUp", "sootexampleTestJUnit3", "test", target, classPath);
       Method tearDown = testselector.util.Util.findMethod("tearDown", "sootexampleTestJUnit3", "test", target, classPath);

        Assert.assertTrue(testselector.util.Util.isJunitTestCase(test1,5 ));
        Assert.assertTrue(testselector.util.Util.isJunitTestCase(test2,5 ));


        Assert.assertEquals(null, test3);
/*
        SootClass sc = new SootClass("out\\production\\Junit3Test");
        SootMethod sm = sc.getMethodByName("setUp");
        Assert.assertTrue(testselector.util.Util.isATestMethod(sm));
*/
    }

    @Test
    public void findJunit5Test() {

        ArrayList<String> target = new ArrayList<>();
        target.add("out/production/Junit5Test");
        ArrayList<String> classPath = new ArrayList<>();
        classPath.add("");
       Method succeedingStandardTest = testselector.util.Util.findMethod("succeedingStandardTest", "sootexampleTestJUnit5", "test", target,classPath);
       Method succeedingGroupedTest = testselector.util.Util.findMethod("succeedingGroupedTest", "sootexampleTestJUnit5", "test", target,classPath);
       Method testNotExistent = testselector.util.Util.findMethod("testNotExisting", "sootexampleTestJUnit5", "test", target,classPath);

        Assert.assertTrue(testselector.util.Util.isJunitTestCase(succeedingGroupedTest,5 ));
        Assert.assertTrue(testselector.util.Util.isJunitTestCase(succeedingStandardTest, 5));
        Assert.assertEquals(null, testNotExistent);

        Object string = new String();
        testselector.util.Util.isJunitTestCase(string,5 );
        Assert.assertEquals(false, testselector.util.Util.isJunitTestCase(string,5 ));
    }


}
