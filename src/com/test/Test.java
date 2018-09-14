package com.test;

import com.company.Project;
import com.company.Util;
import fj.data.Array;
import org.junit.Assert;
import org.junit.Ignore;

import java.lang.reflect.Method;
import java.util.*;

public class Test {
    private static HashMap<Method, ArrayList<String>> m;
    private static Project p;
    private static Project p1;

    @org.junit.BeforeClass
    public static void  setUp(){
        p = new Project("C:\\Users\\Dario\\IdeaProjects\\soot test call graph p\\out\\production\\soot test call graph p");
        p1 = new Project("C:\\Users\\Dario\\IdeaProjects\\soot test call graph p1\\out\\production\\soot test call graph p1");
       m = Util.findChange(p, p1);

    }

    @org.junit.Test
    public void load2ProjectClasses(){
        Assert.assertTrue(!p.getProjectClasses().isEmpty());

    }

    @org.junit.Test
    public void loadProjectClasses(){
         Assert.assertTrue(!p1.getProjectClasses().isEmpty());

    }


    /*
    @org.junit.Test
    public void utilTestTest(){
        Util.runTest(p1);
    }
    */

    @org.junit.Test
    public void utilTestTestReal(){
      //  Util.runTestMethods(p1, Util.findChange(p,p1));
    }

    /*@org.junit.Test
    public void utilTestFindDifferentMethodC(){
        Set<String> keys =  m.keySet();
        for(String key : keys){
            ArrayList<String> valuesForKey = m.get(key);
            for(String value : valuesForKey){
                System.out.println(key + " " + value);
            }
        }

    }
    */

    @org.junit.Test
    public void utilTestFindMethod(){
        Util.findChange(p,p1);
    }

    @org.junit.Test
    public void utilTestRunMethod(){
        Util.runTestMethods(p1.getPath(),m.keySet() );
    }

    @org.junit.Test
    public void utilTestDifferenceInAPrivateMethod(){
       Set<Method> runned =  Util.runTestMethods(p1.getPath(), m.keySet());
       boolean check = false;
       for(Method m : runned){
           if(m.getName().equals("testDifferenceInAPrivateMethod"))
               check = true;
       }
       Assert.assertTrue(check);
    }

    @org.junit.Test
    //Non lo so.
    public void utilTestFindChangeInAPrivateMethod(){
        Collection<ArrayList<String>> values =   m.values();
        boolean check = false;
        Iterator<ArrayList<String>> listIterator =  values.iterator();
        while (listIterator.hasNext()){
            ArrayList<String> value = listIterator.next();
            if(value.contains("privateMethodWithChange"))
                check = true;
        }
        Assert.assertTrue(check);
    }

    @org.junit.Test
    //Questo test deve essere false poichè un metodo con una signature diversa diventa come un metodo nuovo, quindi non viene selezionato, poichè
    //per ora selezioniamo solo quei test che testano metodi già presenti in p ma modificati, diversa signature = metodo diverso = in p non c'è = non selezionato.
    public void utilTestFindChangeInSignature(){
        Collection<ArrayList<String>> values =   m.values();
        boolean check = false;
        Iterator<ArrayList<String>> listIterator =  values.iterator();
        while (listIterator.hasNext()){
            ArrayList<String> value = listIterator.next();
            if(value.contains("methodWithDifferentSignature"))
                check = true;
        }
        Assert.assertFalse(check);
    }


    @org.junit.Test
    //Perchè non fallisce? Poichè il metodo che chiama "methodWithDifferentSignature", chiama questo metodo in p in un modo (virtual invoke) e in p1 in un altro modo (special invoke)
    //quindi la differenza nella signature del metodo che in p è "protected" mentre in p1 è "private" cambia come questo metodo ciene invocato e di conseguenza anche il metodo stesso che chiama questo metodo risulta diverso in p e p1.
   //QUindi testo anhe i metodi che chiamano altri metodi a cui è stata cambiata a signature.
    public void utilTestChangeInSignature(){
        Set<Method> runned =  Util.runTestMethods(p1.getPath(), m.keySet());
        boolean check = false;
        for(Method m : runned){
            if(m.getName().equals("testDifferenceInSignature"))
                check = true;
        }
        Assert.assertTrue(check);
    }

    @org.junit.Test
    //Se il metodo non c'è significa che il cambiamento di un nome di una variabile non comporta un cambiamento per soot.
    //es. [...] m(){ int x = 3-4;}
    //    [...] m1() {int j = 3-4}
    //m ed m1 risultano giustamnente uguali.
    public void utilTestDifferentNameOfAVariable(){
        Set<Method> runned =  Util.runTestMethods(p1.getPath(), m.keySet());
        boolean check = false;
        for(Method m : runned){
            if(m.getName().equals("testDifferentNameOfAVariable"))
                check = true;
        }
        Assert.assertFalse(check);
    }

    @org.junit.Test
    //Se il metodo non c'è significa che il cambiamento di un nome di una variabile non comporta un cambiamento per soot.
    //es. [...] m(){ int x = 3-4;}
    //    [...] m1() {int j = 3-4}
    //m ed m1 risultano giustamnente uguali.
    public void utilTestFindDifferenInNameOfAVariable(){
        Collection<ArrayList<String>> values =   m.values();
        boolean check = false;
        Iterator<ArrayList<String>> listIterator =  values.iterator();
        while (listIterator.hasNext()){
            ArrayList<String> value = listIterator.next();
            if(value.contains("methodWithDifferenceInVariableName"))
                check = true;
        }
        Assert.assertFalse(check);
    }







}
