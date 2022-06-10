package CATTO;

import org.junit.Ignore;
import soot.SootMethod;
import CATTO.test.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestTest {

    @org.junit.Test
    public void testDifferentObject(){
        SootMethod m = mock(SootMethod.class);
        Test t = new Test(m);
        assertNotEquals(t, new Object());
    }

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
        assertNotEquals("", t);
    }

    @org.junit.Test
    public void  testHash(){
        SootMethod m = mock(SootMethod.class);
        Test t = new Test(m);
        Test t1 = new Test(m);
        assertEquals(t.hashCode(), t1.hashCode());
    }

    @org.junit.Test
    @Ignore
    public void  testHash11(){
        SootMethod m = mock(SootMethod.class);
        SootMethod m1 = mock(SootMethod.class);
        String s = "methodtested";
        HashSet<String> hs = new HashSet<>();
        hs.add(s);
        Test t = mock(Test.class);
        Test t1 = mock(Test.class);
        when(t.getTestMethod()).thenReturn(m);
        when(t1.getTestMethod()).thenReturn(m1);
        when(t.getTestingMethods()).thenReturn(hs);    ;

        assertNotEquals(t.hashCode(), t1.hashCode());
    }

}
