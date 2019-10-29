package project;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import soot.SootMethod;
import testSelector.project.NewProject;
import testSelector.project.PreviousProject;
import testSelector.testSelector.FromTheBottom;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


public class NewProjectTest {
    private static final String[] classPath = {"C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\jce.jar;C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar"};

    @Test
    public void noEntryPoints() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, testselector.exception.NoTestFoundedException, IOException {

        Assertions.assertThrows(testselector.exception.NoTestFoundedException.class, () ->  new NewProject(4, classPath, "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out" + File.separator + File.separator + "production" + File.separator + File.separator + "p1"));

    }

    @Test
    public void copiedMethods() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, testselector.exception.NoTestFoundedException, IOException {
        PreviousProject PREVIOUS_VERSION_PROJECT = new PreviousProject(4, classPath, "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out" + File.separator + File.separator + "production" + File.separator + File.separator + "p", "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out" + File.separator + File.separator + "test" + File.separator + File.separator + "p");
        NewProject NEW_VERSION_PROJECT = new NewProject(4, classPath, "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out" + File.separator + File.separator + "production" + File.separator + File.separator + "p1", "C:\\Users\\Dario\\IdeaProjects\\whatTestProjectForTesting\\out" + File.separator + File.separator + "test" + File.separator + File.separator + "p1");

        FromTheBottom ts = new FromTheBottom(PREVIOUS_VERSION_PROJECT, NEW_VERSION_PROJECT);
        AtomicBoolean isIn = new AtomicBoolean(false);
        AtomicBoolean isIn2 = new AtomicBoolean(false);
        Set<testSelector.testSelector.Test> testSelected = ts.selectTest();

        testSelected.forEach(test -> {
            for(SootMethod m : NEW_VERSION_PROJECT.getApplicationMethod()){
                if(m.getName().equals(test.getTestMethod().getName()) && m.getDeclaringClass().getName().equals(test.getTestMethod().getDeclaringClass().getName()) && m.getName().equals(test.getTestMethod().getName()) && m.getSubSignature().equals(test.getTestMethod().getSubSignature()) ){
                    Assertions.assertAll(
                            () -> {
                                Assertions.assertEquals(m.getExceptions(), test.getTestMethod().getExceptions());                                             Assertions.assertEquals(m.getReturnType(), test.getTestMethod().getReturnType());
                                Assertions.assertEquals(m.isPhantom(), test.getTestMethod().isPhantom());                                                     Assertions.assertEquals(m.getNumber(), test.getTestMethod().getNumber());
                                Assertions.assertEquals(m.getSource(), test.getTestMethod().getSource());                                                     Assertions.assertEquals(m.isDeclared(), test.getTestMethod().isDeclared());
                                Assertions.assertEquals(m.getActiveBody().toString(), test.getTestMethod().getActiveBody().toString());

                            }

                    );

                }


            }

            //todo: da rivedere.
            if(test.getTestMethod().getName().equals("concreteMethodThatTestDifferentMethodNotOverrided")) {
                Assertions.assertTrue(test.getTestMethod().getDeclaringClass().getName().equals("ExtendedAbstractClass") || test.getTestMethod().getDeclaringClass().getName().equals("ExtendedAbstractClass2"));
            isIn.set(true);
            }

            if(test.getTestMethod().getName().equals("concreteDifferentMethodThatTestDifferentMethodNotOverrided")) {
                Assertions.assertTrue(test.getTestMethod().getDeclaringClass().getName().equals("ExtendedAbstractClass") || test.getTestMethod().getDeclaringClass().getName().equals("ExtendedAbstractClass2"));
                 isIn2.set(true);

            }

        });
        Assertions.assertTrue(isIn.get() && isIn2.get());

         isIn.set(false);
        for(testSelector.testSelector.Test t : testSelected){
            if(t.getTestMethod().getName().equals("concreteMethodOverriddenNotTaggedWithTest")){
                Assertions.assertTrue(t.getTestMethod().getDeclaringClass().getName().equals( "ExtendedAbstractClass") || t.getTestMethod().getDeclaringClass().getName().equals( "ExtendedAbstractClass2") );
                isIn.set(true);
            }
        }
        Assertions.assertTrue(isIn.get());

    }



}
