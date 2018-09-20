package com.test;

import com.company.Project;
import com.company.TestSelector;
import com.company.Util;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

public class TestRunJunit3TestCase {

    @Test
    public void runJunit3Test() {
        Project p = mock(Project.class);
        Project p1 = mock(Project.class);
        TestSelector t = new TestSelector(p, p1);
        TestSelector tSpy = spy(t);
        Set<Method> Junit3Test = new HashSet();
        String path = "out/production/Junit3Test";

        Method test1 = Util.findMethod("testFail", "sootexampleTestJUnit3", "test", path);
        Method test2 = Util.findMethod("testPass", "sootexampleTestJUnit3", "test", path);
        Junit3Test.add(test1);
        Junit3Test.add(test2);

        doReturn(Junit3Test).when(tSpy).getAllTestToRun();

        when(p1.getPath()).thenReturn(path);

        Set<Method> runned = tSpy.runTestMethods();
        Assert.assertTrue(runned.contains(test1));
        Assert.assertTrue(runned.contains(test2));

    }
}
