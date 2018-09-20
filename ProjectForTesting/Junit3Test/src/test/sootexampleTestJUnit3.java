package test;

import junit.framework.Assert;
import junit.framework.TestCase;

public class sootexampleTestJUnit3 extends TestCase {

    public void testFail() {
        Assert.assertEquals(2, 1);
    }

    public void testPass() {
        Assert.assertEquals(2, 2);
    }

}
