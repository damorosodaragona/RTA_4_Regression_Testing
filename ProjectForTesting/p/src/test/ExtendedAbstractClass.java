package test;

import org.junit.Assert;
import org.junit.Test;
import sootTest.sootexample;

public class ExtendedAbstractClass extends AbstractTestClass {

    @Override
    @Test
    public void abstractMethod() {
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
