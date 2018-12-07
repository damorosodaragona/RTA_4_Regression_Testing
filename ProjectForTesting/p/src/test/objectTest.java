package test;

import org.junit.BeforeClass;
import org.junit.Test;
import sootTest.object;

import static org.junit.Assert.assertEquals;

public class objectTest {
    private object ob;

    @BeforeClass
    public void setUp() {
        ob = new object();
    }

    @Test
    public void testField() {
        assertEquals("final field", ob.getFinalField());

    }
}
