package testselector;


import org.apache.log4j.BasicConfigurator;
import org.junit.Test;
import soot.SootClass;
import soot.SootMethod;
import testSelector.project.PreviousProject;
import testselector.exception.NoTestFoundedException;

import java.io.File;
import java.nio.file.NotDirectoryException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRunJunit3TestCase {

    @Test
    public void runJunit3Test() {
        BasicConfigurator.configure();

        SootMethod sootMethodMock = mock(SootMethod.class);
        String[] classPath = {"C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\jce.jar;C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\3.8.2\\junit-3.8.2.jar"};

        try {
            PreviousProject PREVIOUS_VERSION_PROJECT = new PreviousProject(3, classPath, "..\\whatTestProjectForTesting\\out" + File.separator + File.separator + "production" + File.separator + File.separator + "Junit3Test");
        } catch (NoTestFoundedException e) {
            e.printStackTrace();
        } catch (NotDirectoryException e) {
            e.printStackTrace();
        }
        when(sootMethodMock.getName()).thenReturn("testFail");
        sootMethodMock.setDeclaringClass(new SootClass("sootexampleTestJUnit3"));
        when(sootMethodMock.getDeclaringClass()).thenReturn(new SootClass("sootexampleTestJUnit3"));

        testSelector.testSelector.Test t = new testSelector.testSelector.Test(sootMethodMock);
        t.runTest();


    }


}
