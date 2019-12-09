package junitrunner;

import org.junit.Before;
import org.junit.Test;
import soot.SootClass;
import soot.SootMethod;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRunJunit3TestCase {
    private SootMethod sootMethodMock;
    private List<String> targetPath;

    @Before
    public  void setUp(){


        sootMethodMock = mock(SootMethod.class);

        sootMethodMock.setDeclaringClass(new SootClass("sootexampleTestJUnit3"));
        when(sootMethodMock.getDeclaringClass()).thenReturn(new SootClass("sootexampleTestJUnit3"));

        targetPath = new ArrayList<String>();
        targetPath.add("C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out\\test\\Junit3Test");




    }

    @Test
    public void runJunit3PassTest() throws NoSuchMethodException, IOException, IllegalAccessException, InvocationTargetException {


        when(sootMethodMock.getName()).thenReturn("testPass");

        testselector.testselector.Test t = new testselector.testselector.Test(sootMethodMock);


        assertEquals(1, Runner.run(t, new String[0], targetPath ).getTestsSucceededCount());

    }

    @Test
    public void runJunit3FailTest() throws NoSuchMethodException, IOException, IllegalAccessException, InvocationTargetException {


        when(sootMethodMock.getName()).thenReturn("testFail");

        testselector.testselector.Test t = new testselector.testselector.Test(sootMethodMock);

        assertEquals(1,Runner.run(t, new String[0], targetPath ).getTestsFailedCount());



    }


}
