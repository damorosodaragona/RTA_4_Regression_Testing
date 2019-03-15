package test;

import org.junit.Assert;
import org.junit.Test;
import sootTest.sootexample;

import java.util.ArrayList;

public class ExtendedAbstractClass2 extends AbsstractTestClass {
    @Override
    public void abstarctMethod() {
        Assert.fail();
    }

    @Override
    public void abstractMethodThatTestDifferentMethod() {
        Assert.fail();
    }

    @Override
    @Test
    public void concreteMethodOverrided(){
        sootexample sc = new sootexample();
        ArrayList k = new ArrayList();
    }
}
