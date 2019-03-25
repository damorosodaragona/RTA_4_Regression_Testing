package test;

import org.junit.Test;
import sootTest.sootexample;

import java.util.ArrayList;

public abstract class AbstractTestClass {

    @Test
    public abstract void abstractMethod();

    @Test
    public void concreteMethodNotOverrided(){
        sootexample sc = new sootexample();
    }

    @Test
    public void concreteMethodOverrided(){
        sootexample sc = new sootexample();
        ArrayList k = new ArrayList();
    }

    @Test
    public abstract void abstractMethodThatTestDifferentMethod();
    @Test
    public void concreteMethodThatTestDifferentMethod(){
        new sootexample().differenceInPrivateMethod();
    }
}
