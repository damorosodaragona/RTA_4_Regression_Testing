package com.test;

import com.company.Project;
import com.company.TestSelector;
import org.junit.Assert;
import org.junit.Ignore;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class Test {
    private static Set<Method> TEST_TO_RUN_FINDED;
    private static Project PREVIOUS_VERSION_PROJECT;
    private static Project NEW_VERSION_PROJECT;
    private static Set<Method> P1_TEST_RUNNED;
    private static Collection<ArrayList<String>> NEW_METHOD_FINDED;
    private static Collection<ArrayList<String>> CHANGED_METHOD_FINDED;


    @org.junit.BeforeClass
    public static void  setUp(){
        PREVIOUS_VERSION_PROJECT = new Project("C:\\Users\\Dario\\IdeaProjects\\soot test call graph p\\out\\production\\soot test call graph p");
        NEW_VERSION_PROJECT = new Project("C:\\Users\\Dario\\IdeaProjects\\soot test call graph p1\\out\\production\\soot test call graph p1");
        TestSelector u = new TestSelector(PREVIOUS_VERSION_PROJECT, NEW_VERSION_PROJECT);
        TEST_TO_RUN_FINDED = u.selectTest();
        P1_TEST_RUNNED = u.runTestMethods();
        CHANGED_METHOD_FINDED = u.getChangedMethods();
        NEW_METHOD_FINDED = u.getNewOrRemovedMethods();
    }

    @org.junit.Test
    public void load2ProjectClasses(){
        Assert.assertTrue(!PREVIOUS_VERSION_PROJECT.getProjectClasses().isEmpty());

    }

    @org.junit.Test
    public void loadProjectClasses(){
        Assert.assertTrue(!NEW_VERSION_PROJECT.getProjectClasses().isEmpty());

    }

    @org.junit.Test
    public void utilTestDifferenceInAPrivateMethod(){
        boolean check = false;
        for (Method m : P1_TEST_RUNNED) {
           if(m.getName().equals("testDifferenceInAPrivateMethod"))
               check = true;
       }
       Assert.assertTrue(check);
    }

    @org.junit.Test
    //Non lo so.
    public void utilTestFindChangeInAPrivateMethod(){
        boolean check = false;
        Iterator<ArrayList<String>> listIterator = CHANGED_METHOD_FINDED.iterator();
        while (listIterator.hasNext()){
            ArrayList<String> value = listIterator.next();
            if(value.contains("privateMethodWithChange"))
                check = true;
        }
        Assert.assertTrue(check);
    }

    @org.junit.Test
    //Questo test deve essere false poichè un metodo con una signature diversa diventa come un metodo nuovo, quindi non viene selezionato, poichè
    //per ora selezioniamo solo quei test che testano metodi già presenti in PREVIOUS_VERSION_PROJECT ma modificati, diversa signature = metodo diverso = in PREVIOUS_VERSION_PROJECT non c'è = non selezionato.
    public void utilTestFindChangeInSignature(){
        boolean check = false;
        Iterator<ArrayList<String>> listIterator = NEW_METHOD_FINDED.iterator();
        while (listIterator.hasNext()){
            ArrayList<String> value = listIterator.next();
            if(value.contains("methodWithDifferentSignature"))
                check = true;
        }
        Assert.assertTrue(check);
    }


    @org.junit.Test
    //Perchè non fallisce? Poichè il metodo che chiama "methodWithDifferentSignature", chiama questo metodo in PREVIOUS_VERSION_PROJECT in un modo (virtual invoke) e in NEW_VERSION_PROJECT in un altro modo (special invoke)
    //quindi la differenza nella signature del metodo che in PREVIOUS_VERSION_PROJECT è "protected" mentre in NEW_VERSION_PROJECT è "private" cambia come questo metodo ciene invocato e di conseguenza anche il metodo stesso che chiama questo metodo risulta diverso in PREVIOUS_VERSION_PROJECT e NEW_VERSION_PROJECT.
   //QUindi testo anhe i metodi che chiamano altri metodi a cui è stata cambiata a signature.
    public void utilTestChangeInSignature(){

        boolean check = false;
        for (Method m : P1_TEST_RUNNED) {
            if(m.getName().equals("testDifferenceInSignature"))
                check = true;
        }
        Assert.assertTrue(check);
    }

    @org.junit.Test
    //Se il metodo non c'è significa che il cambiamento di un nome di una variabile non comporta un cambiamento per soot.
    //es. [...] TEST_TO_RUN_FINDED(){ int x = 3-4; }
    //    [...] m1(){ int j = 3-4; }
    //TEST_TO_RUN_FINDED ed m1 risultano giustamnente uguali.
    public void utilTestDifferentNameOfAVariable(){

        boolean check = false;
        for (Method m : P1_TEST_RUNNED) {
            if(m.getName().equals("testDifferentNameOfAVariable"))
                check = true;
        }
        Assert.assertFalse(check);
    }

    @org.junit.Test
    //Se il metodo non c'è significa che il cambiamento di un nome di una variabile non comporta un cambiamento per soot.
    //es. [...] TEST_TO_RUN_FINDED() { int x = 3-4; }
    //    [...] m1() { int j = 3-4; }
    //TEST_TO_RUN_FINDED ed m1 risultano giustamnente uguali.
    public void utilTestFindDifferenInNameOfAVariable(){
        boolean check = false;
        Iterator<ArrayList<String>> listIterator = CHANGED_METHOD_FINDED.iterator();

        while (listIterator.hasNext()){
            ArrayList<String> value = listIterator.next();
            if(value.contains("methodWithDifferenceInVariableName"))
                check = true;
        }
        Assert.assertFalse(check);
    }

    @Ignore
    @org.junit.Test(expected = IllegalStateException.class)
    public void IllegalStateExceptionTest() {

        TestSelector u = new TestSelector(PREVIOUS_VERSION_PROJECT, NEW_VERSION_PROJECT);
        u.runTestMethods();

    }

    @org.junit.Test
    public void newMethodTest() {

        boolean check = false;
        Iterator<ArrayList<String>> listIterator = NEW_METHOD_FINDED.iterator();
        while (listIterator.hasNext()) {
            ArrayList<String> value = listIterator.next();
            if (value.contains("newMethod"))
                check = true;
        }
        Assert.assertTrue(check);


    }

    @org.junit.Test
    public void newMethodCheckTest() {

        boolean check = false;
        Iterator<Method> listIterator = TEST_TO_RUN_FINDED.iterator();
        while (listIterator.hasNext()) {
            Method value = listIterator.next();
            if (value.getName().equals("testNewMethod"))
                check = true;
        }
        Assert.assertTrue(check);


    }










}
