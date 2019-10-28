package util;

import org.junit.Assert;
import org.junit.Test;
import testSelector.util.Util;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class UtilTest {


    @Test
    public void findJunit3Test() {
        ArrayList<String> target = new ArrayList<>();
        target.add("out/production/Junit3Test");
        ArrayList<String> classPath = new ArrayList<>();
        classPath.add("");
        Method test1 = Util.findMethod("testFail", "sootexampleTestJUnit3", "test", target, classPath);
       Method test2 = Util.findMethod("testPass", "sootexampleTestJUnit3", "test", target, classPath);
       Method test3 = Util.findMethod("testNotExistent", "sootexampleTestJUnit3", "test", target, classPath);


        Assert.assertTrue(Util.isJunitTestCase(test1,3 ));
        Assert.assertTrue(Util.isJunitTestCase(test2,3 ));
        Assert.assertEquals(null, test3);


    }

    @Test
    public void findJunit5Test() {

        ArrayList<String> target = new ArrayList<>();
        target.add("out/production/Junit5Test");
        ArrayList<String> classPath = new ArrayList<>();
        classPath.add("");
       Method succeedingStandardTest = Util.findMethod("succeedingStandardTest", "sootexampleTestJUnit5", "test", target,classPath);
       Method succeedingGroupedTest = Util.findMethod("succeedingGroupedTest", "sootexampleTestJUnit5", "test", target,classPath);
       Method testNotExistent = Util.findMethod("testNotExisting", "sootexampleTestJUnit5", "test", target,classPath);

        Assert.assertTrue(Util.isJunitTestCase(succeedingGroupedTest,5 ));
        Assert.assertTrue(Util.isJunitTestCase(succeedingStandardTest, 5));
        Assert.assertEquals(null, testNotExistent);

        Object string = new String();
        Util.isJunitTestCase(string,5 );
        Assert.assertEquals(false, Util.isJunitTestCase(string,5 ));
    }


}
