package test;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;
import soot.SootClass;
import soot.SootMethod;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRunJunit3TestCase {

    @Test
    public void runJunit3PassTest() {
        BasicConfigurator.configure();

        SootMethod sootMethodMock = mock(SootMethod.class);

        when(sootMethodMock.getName()).thenReturn("testPass");
        sootMethodMock.setDeclaringClass(new SootClass("tsTest.scenaries.ExampleTestJUnit3"));
        when(sootMethodMock.getDeclaringClass()).thenReturn(new SootClass("tsTest.scenaries.ExampleTestJUnit3"));

        testSelector.testSelector.Test t = new testSelector.testSelector.Test(sootMethodMock);
        assertEquals(1, t.runTest().getTestsSucceededCount());

    }

    @Test
    public void runJunit3FailTest() {
        BasicConfigurator.configure();

        SootMethod sootMethodMock = mock(SootMethod.class);

        when(sootMethodMock.getName()).thenReturn("testFail");
        sootMethodMock.setDeclaringClass(new SootClass("tsTest.scenaries.ExampleTestJUnit3"));
        when(sootMethodMock.getDeclaringClass()).thenReturn(new SootClass("tsTest.scenaries.ExampleTestJUnit3"));

        testSelector.testSelector.Test t = new testSelector.testSelector.Test(sootMethodMock);
        assertEquals(1, t.runTest().getTestsFailedCount());



    }


}
