package test;

import org.junit.Assert;
import org.junit.Test;
import sootTest.sootexample;

import java.util.ArrayList;

public class ExtendedAbstractClass2 extends AbstractTestClass {
    @Override
    public void abstractMethod() {
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

    @Test
    @Override
    public void concreteMethodOverridedOnlyByExtendAbstractClass2(){
        sootexample sc = new sootexample();
        ArrayList k = new ArrayList();
    }
}
