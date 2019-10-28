package test;

import soot.SootMethod;
import testSelector.testSelector.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

public class TestTest {

    @org.junit.Test
    public void  testEqual(){
        SootMethod m = mock(SootMethod.class);
        Test t = new Test(m);
        Test t1 = new Test(m);
        assertEquals(t, t1);
    }

    @org.junit.Test
    public void  testEqual1(){
        SootMethod m = mock(SootMethod.class);
        SootMethod m1 = mock(SootMethod.class);
        Test t = new Test(m);
        Test t1 = new Test(m1);
        assertNotEquals(t, t1);
    }
    @org.junit.Test
    public void  testEqual2(){
        SootMethod m = mock(SootMethod.class);
        Test t = new Test(m);
        assertEquals(t, t);
    }
    @org.junit.Test
    public void  testEqual3(){
        SootMethod m = mock(SootMethod.class);
        Test t = new Test(m);
        assertNotEquals(t, "");
    }

    @org.junit.Test
    public void  testHash(){
        SootMethod m = mock(SootMethod.class);
        Test t = new Test(m);
        Test t1 = new Test(m);
        assertEquals(t.hashCode(), t1.hashCode());
    }

    @org.junit.Test
    public void  testHash11(){
        SootMethod m = mock(SootMethod.class);
        SootMethod m1 = mock(SootMethod.class);
        Test t = new Test(m);
        Test t1 = new Test(m1);
        assertNotEquals(t.hashCode(), t1.hashCode());
    }

}
