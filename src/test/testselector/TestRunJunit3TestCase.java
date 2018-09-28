package testselector;


import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Test;
import testselector.util.Util;

import java.util.ArrayList;

public class TestRunJunit3TestCase {

    @Test
    public void runJunit3Test() {
        BasicConfigurator.configure();


        ArrayList<String> path = new ArrayList<>();
        path.add("out/production/Junit3Test");

        testselector.testSelector.Test test1 = new testselector.testSelector.Test(Util.findMethod("testFail", "sootexampleTestJUnit3", "test", path));
        testselector.testSelector.Test test2 = new testselector.testSelector.Test(Util.findMethod("testPass", "sootexampleTestJUnit3", "test", path));

        test1.runTest().getTestsSucceededCount();
        Assert.assertEquals(0, test1.runTest().getTestsSucceededCount());
        Assert.assertEquals(1, test1.runTest().getTestsFailedCount());
        Assert.assertEquals(1, test2.runTest().getTestsSucceededCount());
        Assert.assertEquals(0, test2.runTest().getTestsFailedCount());

    }


}
