package tsTest.scenaries;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;

public class ExampleTestJUnit4 {

    ArrayList<String> strings;


    @Before
    public void setUp(){
        strings = new ArrayList<>();
        strings.add("fail");
        strings.add("pass");
    }

    @Test
    public void testFail(){
        assertTrue(strings.contains("failed"));
    }

    @Test
    public void testPass(){
        assertTrue(strings.contains("pass"));
    }

    @After
    public void tearDown(){
        strings.clear();
    }
}
