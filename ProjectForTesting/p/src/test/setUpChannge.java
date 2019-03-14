package test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sootTest.sootexample;

import static junit.framework.TestCase.fail;

public class setUpChannge {
    @Before
    public void setUp(){
        sootexample sc = new sootexample();

    }

    @Test
    public void toAddForChangeInSetUpEqual(){
        fail("to implement");
    }

    @Test
    public void toAddForChangeInSetUpDifferent(){
        Assert.fail();
    }
}
