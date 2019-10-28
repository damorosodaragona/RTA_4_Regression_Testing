package tsTest.scenaries;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ExampleTestJUnit3 extends TestCase {

    int i;

    public void testFail() {
        Assert.assertEquals(1, i);
    }

    public void testPass() {
        Assert.assertEquals(2, i);
    }

    public void setUp() {
        i = 2;
    }

}
