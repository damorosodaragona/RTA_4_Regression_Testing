package test;

import org.junit.Before;
import org.junit.Test;
import sootTest.sootexample;


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
    public void test3() {
        st = new sootexample();

    }

    @Test
    public void testInit() {
        st = new sootexample();

    }
    @Test
    public void test4() {
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
    public void testFinalDifferntMethod() {
        st.differentFinalMethod();
    }

    @Test
    public void testFinalStaticEqualMethod() {
        sootexample.equalStaticFinalMethod();
    }
}


