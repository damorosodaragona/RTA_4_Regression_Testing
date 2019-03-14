package test;

import org.junit.Assert;
import org.junit.Test;
import sootTest.sootexample;

public class ExtendedAbstractClass extends AbsstractTestClass {

    @Override
    @Test
    public void abstarctMethod() {
        Assert.assertTrue(false);
    }

    @Override
    @Test
    public void concreteMethodOverrided(){
        Assert.fail();
    }

    @Override
    @Test
    public void abstractMethodThatTestDifferentMethod() {
        sootexample.differentStaticFinalMethod();
    }
}
