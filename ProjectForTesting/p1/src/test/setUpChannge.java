package test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.fail;

public class setUpChannge {
    @Before
    public void setUp(){
       new ArrayList<>();
    }

    @Test
    public void toAddForChangeInSetUpEqual(){
        fail("to implement");
    }

    @Test
    public void toAddForChangeInSetUpDifferent(){
        Assert.assertTrue(true);
    }


}
