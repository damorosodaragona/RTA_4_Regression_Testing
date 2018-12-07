package testselector;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;
import testselector.project.Project;
import testselector.testSelector.Test;

import java.io.File;
import java.nio.file.NotDirectoryException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TestSelector {
    private static Set<Test> TEST_TO_RUN_FINDED;
    private static Project PREVIOUS_VERSION_PROJECT;
    private static Project NEW_VERSION_PROJECT;
    private static Collection<Set<String>> NEW_METHOD_FINDED;
    private static Collection<Set<String>> CHANGED_METHOD_FINDED;


    @org.junit.BeforeClass
    public static void setUp() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        BasicConfigurator.configure();

        PREVIOUS_VERSION_PROJECT = new Project("out" + File.separator + File.separator + "production" + File.separator + File.separator + "p");
        NEW_VERSION_PROJECT = new Project("out" + File.separator + File.separator + "production" + File.separator + File.separator + "p1");
        PREVIOUS_VERSION_PROJECT.saveCallGraph("ProjectForTesting", "old");
        NEW_VERSION_PROJECT.saveCallGraph("ProjectForTesting", "new");

        testselector.testSelector.TestSelector u = new testselector.testSelector.TestSelector(PREVIOUS_VERSION_PROJECT, NEW_VERSION_PROJECT);
        TEST_TO_RUN_FINDED = u.selectTest();
        CHANGED_METHOD_FINDED = u.getChangedMethods();
        NEW_METHOD_FINDED = u.getNewOrRemovedMethods();
    }

    @org.junit.Test
    public void load2ProjectClasses() {
        Assert.assertTrue(!PREVIOUS_VERSION_PROJECT.getProjectClasses().isEmpty());

    }

    @org.junit.Test
    public void loadProjectClasses() {
        Assert.assertTrue(!NEW_VERSION_PROJECT.getProjectClasses().isEmpty());

    }

    @org.junit.Test
    public void utilTestDifferenceInAPrivateMethod() {
        boolean check = false;
        for (Test t : TEST_TO_RUN_FINDED) {
            if ("testDifferenceInAPrivateMethod".equals(t.getTestMethod().getName()))
                check = true;
        }
        Assert.assertTrue(check);
    }

    @org.junit.Test
    //Non lo so.
    public void utilTestFindChangeInAPrivateMethod() {
        boolean check = false;
        for (Set<String> value : CHANGED_METHOD_FINDED) {
            if (value.contains("privateMethodWithChange"))
                check = true;
        }
        Assert.assertTrue(check);
    }


    @org.junit.Test
    //Questo test deve essere false poichè un metodo con una signature diversa diventa come un metodo nuovo, quindi non viene selezionato, poichè
    //per ora selezioniamo solo quei test che testano metodi già presenti in PREVIOUS_VERSION_PROJECT ma modificati, diversa signature = metodo diverso = in PREVIOUS_VERSION_PROJECT non c'è = non selezionato.
    public void utilTestFindChangeInSignature() {
        boolean check = false;
        for (Set<String> value : NEW_METHOD_FINDED) {
            if (value.contains("methodWithDifferentSignature"))
                check = true;
        }
        Assert.assertTrue(!check);
    }


    @org.junit.Test
    //Perchè non fallisce? Poichè il metodo che chiama "methodWithDifferentSignature", chiama questo metodo in PREVIOUS_VERSION_PROJECT in un modo (virtual invoke) e in NEW_VERSION_PROJECT in un altro modo (special invoke)
    //quindi la differenza nella signature del metodo che in PREVIOUS_VERSION_PROJECT è "protected" mentre in NEW_VERSION_PROJECT è "private" cambia come questo metodo ciene invocato e di conseguenza anche il metodo stesso che chiama questo metodo risulta diverso in PREVIOUS_VERSION_PROJECT e NEW_VERSION_PROJECT.
    //QUindi testo anhe i metodi che chiamano altri metodi a cui è stata cambiata a signature.
    public void utilTestChangeInSignature() {

        boolean check = false;
        for (Test t : TEST_TO_RUN_FINDED) {
            if ("testDifferenceInSignature".equals(t.getTestMethod().getName()))
                check = true;
        }
        Assert.assertTrue(check);
    }

    @org.junit.Test
    //Se il metodo non c'è significa che il cambiamento di un nome di una variabile non comporta un cambiamento per soot.
    //es. [...] TEST_TO_RUN_FINDED(){ int x = 3-4; }
    //    [...] m1(){ int j = 3-4; }
    //TEST_TO_RUN_FINDED ed m1 risultano giustamnente uguali.
    public void utilTestDifferentNameOfAVariable() {

        boolean check = false;
        for (Test t : TEST_TO_RUN_FINDED) {
            if ("testDifferentNameOfAVariable".equals(t.getTestMethod().getName()))
                check = true;
        }
        Assert.assertFalse(check);
    }

    @org.junit.Test
    //Se il metodo non c'è significa che il cambiamento di un nome di una variabile non comporta un cambiamento per soot.
    //es. [...] TEST_TO_RUN_FINDED() { int x = 3-4; }
    //    [...] m1() { int j = 3-4; }
    //TEST_TO_RUN_FINDED ed m1 risultano giustamnente uguali.
    public void utilTestFindDifferenInNameOfAVariable() {
        boolean check = false;

        for (Set<String> value : CHANGED_METHOD_FINDED) {
            if (value.contains("methodWithDifferenceInVariableName"))
                check = true;
        }
        Assert.assertFalse(check);
    }

    @org.junit.Test
    public void newMethodTest() {

        boolean check = false;
        Iterator<Set<String>> listIterator = NEW_METHOD_FINDED.iterator();
        while (listIterator.hasNext()) {
            HashSet<String> value = (HashSet<String>) listIterator.next();
            if (value.contains("newMethod"))
                check = true;
        }
        Assert.assertTrue(check);
        check = false;
        listIterator = CHANGED_METHOD_FINDED.iterator();
        while (listIterator.hasNext()) {
            HashSet<String> value = (HashSet<String>) listIterator.next();
            if (value.contains("newMethod"))
                check = true;
        }
        Assert.assertFalse(check);

    }

    @org.junit.Test
    public void newMethodCheckTest() {

        boolean check = false;
        for (Test t : TEST_TO_RUN_FINDED) {
            if ("testNewMethod".equals(t.getTestMethod().getName()))
                check = true;
        }
        Assert.assertTrue(check);


    }

    @org.junit.Test
    public void constantTest() {

        boolean check = false;
        for (Test t : TEST_TO_RUN_FINDED) {
            if ("testField".equals(t.getTestMethod().getName()))
                check = true;
        }
        Assert.assertTrue(check);


    }


}
