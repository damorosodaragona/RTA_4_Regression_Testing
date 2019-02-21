package test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sootTest.sootexample;

import java.util.ArrayList;


public class sootexampleTest {
    sootexample st;


    @Before
    public void setUp() {
        st = new sootexample();
    }


    @Test
    public void test1() {

        st.d();
    }


    @Test
    public void test2() {
        st.c();
    }

    @Test
    public void test2_2() {
        st.c();
    }

    @Test
    public void test3() {
        st = new sootexample();

    }

    @Test
    public void testInit() {
        st = new sootexample();

    }

    @Test
    public void testDifferenceInAPrivateMethod() {
        st.differenceInPrivateMethod();

    }

    @Test
    public void testDifferenceInSignature() {
        st.differenceInSignature();
    }

    @Test
    public void testDifferentNameOfAVariable() {
        st.methodWithDifferenceInVariableName();

    }

    @Test
    public void testNewMethod() {
        st.newMethod();

    }

    @Test
    public void realPassTest() {
        ArrayList<String> a = st.realMethodToTest();
        Assert.assertTrue(a.contains("real"));
    }

    @Test
    public void realFailureTest() {
        ArrayList<String> a = st.realMethodToTest();
        Assert.assertTrue(a.contains("fail"));
    }

    @Test
    public void testStaticEqualMethod() {
        sootexample.equalStaticMethod();
    }

    @Test
    public void testFinalEqualMethod() {
        st.equalFinalMethod();
    }


    @Test
    public void testFinalStaticDifferentMethod() {
        sootexample.differentStaticFinalMethod();
    }

    @Test
    public void testStaticDifferentMethod() {
        sootexample.differentStaticMethod();
    }

    @Test
    public void testFinalDifferentMethod() {
        st.differentFinalMethod();
    }

    @Test
    public void testFinalStaticEqualMethod() {
        sootexample.equalStaticFinalMethod();
    }

    @Test
    public void equalTest() {
        st.c();
        int x = 7;
        boolean condition = false;
        if (x >= 8) {
            condition = true;
        }
        Assert.assertTrue(condition);

    }


}


