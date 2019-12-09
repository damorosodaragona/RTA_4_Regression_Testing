package project;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import soot.SootMethod;
import testselector.exception.InvalidTargetPaths;
import testselector.project.NewProject;
import testselector.project.PreviousProject;
import testselector.testselector.FromTheBottom;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


public class NewProjectTest {
    private static final String[] classPath = {"lib" + File.separator  + "rt.jar;"+ "lib"  + File.separator + "jce.jar;"+"lib" + File.separator + "junit-4.12.jar"};

    @Test
    public void noEntryPoints() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, testselector.exception.NoTestFoundedException, IOException {

        Assertions.assertThrows(testselector.exception.NoTestFoundedException.class, () ->  new NewProject(classPath, "whatTestProjectForTesting" + File.separator + "out" + File.separator + "production" +  File.separator + "p1"));
    }

    @Test
    public void copiedMethods() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, testselector.exception.NoTestFoundedException, IOException, InvalidTargetPaths {
      PreviousProject  PREVIOUS_VERSION_PROJECT = new PreviousProject(classPath, "whatTestProjectForTesting" + File.separator + "out"+ File.separator + "production" + File.separator  + "p", "whatTestProjectForTesting" + File.separator + "out" + File.separator + "test" +  File.separator + "p");

           NewProject NEW_VERSION_PROJECT = new NewProject(classPath, "whatTestProjectForTesting"  + File.separator  + "out"+ File.separator + "production" + File.separator + "p1", "whatTestProjectForTesting" + File.separator  + "out"+ File.separator + "test" + File.separator + File.separator + "p1");


        FromTheBottom ts = new FromTheBottom(PREVIOUS_VERSION_PROJECT, NEW_VERSION_PROJECT);
        AtomicBoolean isIn = new AtomicBoolean(false);
        AtomicBoolean isIn2 = new AtomicBoolean(false);
        Set<testselector.testselector.Test> testSelected = ts.selectTest();

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
        for(testselector.testselector.Test t : testSelected){
            if(t.getTestMethod().getName().equals("concreteMethodOverriddenNotTaggedWithTest")){
                Assertions.assertTrue(t.getTestMethod().getDeclaringClass().getName().equals( "ExtendedAbstractClass") || t.getTestMethod().getDeclaringClass().getName().equals( "ExtendedAbstractClass2") );
                isIn.set(true);
            }
        }
        Assertions.assertTrue(isIn.get());

    }



}
