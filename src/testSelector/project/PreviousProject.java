package testSelector.project;

import soot.*;
import testSelector.util.Util;
import testselector.exception.NoTestFoundedException;

import javax.annotation.Nonnull;
import java.nio.file.NotDirectoryException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PreviousProject extends Project {
    public static String[] PATH;
    public PreviousProject(int junitVersion, String[] classPath, @Nonnull String... target) throws NoTestFoundedException, NotDirectoryException {
        super(junitVersion, classPath, target);

        hierarchy = Scene.v().getActiveHierarchy();
        manageHierarchy();
        Scene.v().releaseActiveHierarchy();
      //  PackManager.v().runPacks();


    }

    private void manageHierarchy() throws NoTestFoundedException {
        HashSet<SootMethod> allTesting;
        HashSet<SootClass> appClass = new HashSet<>(getProjectClasses());
        //for all project classes
        int id = 0;
        for (SootClass s : new HashSet<>(appClass)) {
            //se è un interfaccia o se è astratta vai avanti
            if (Modifier.isInterface(s.getModifiers()) || Modifier.isAbstract(s.getModifiers()))
                continue;
            //se ha sottoclassi (quindi è una superclasse vai avanti -> vogliamo arrivare alla fine della gerarchia)
            if (!Scene.v().getActiveHierarchy().getSubclassesOf(s).isEmpty())
                continue;

            //tutti i test dell'ultima classe della gerarchia
            allTesting = new HashSet<>();
            id++;

            for (SootMethod m : s.getMethods()) {
                //se sono metodi di test aggiungili
                if (Util.isATestMethod(m, getJunitVersion()))
                    allTesting.add(m);
            }

            //fatti dare tutte le superclassi -> in ordine di gerarchia
            List<SootClass> superClasses = Scene.v().getActiveHierarchy().getSuperclassesOf(s);
            //per ogni superclasse
            for (SootClass s1 : superClasses) {
                //se la classe è una classe di libreria skippa
                if (!getProjectClasses().contains(s1))
                    continue;
                //dammi tutti i metodi della superclasse
                List<SootMethod> methods = s1.getMethods();
                //per tutti i metodi della superclasse
                for (SootMethod m1 : methods) {
                    boolean isIn = false;
                    //se non è un test skippa
                    if (!Util.isATestMethod(m1, getJunitVersion()))
                        continue;
                    //per tutti i test già aggiunti
                    for (SootMethod m : new HashSet<>(allTesting)) {
                        //se il metodo nella suprclasse è uguale ad un metodo della foglia (o di una classe sotto nella gerachia)
                        //non aggiungerlo
                        if (m.getSubSignature().equals(m1.getSubSignature())) {
                            isIn = true;
                            break;
                        }
                    }
                    if (!isIn) {
                        //aggiungi il test ereditato
                        allTesting.add(m1);

                    }
                }
            }


            allTesting.forEach(sootMethod -> {
                if (!sootMethod.getDeclaringClass().equals(s)) {
                    SootClass cls = sootMethod.getDeclaringClass();
                    AtomicBoolean toRemove = new AtomicBoolean(false);
                    //   try {
                    s.getMethods().forEach(sootMethod1 -> {
                        if (sootMethod.getSubSignature().equals(sootMethod1.getSubSignature())) {
                            toRemove.set(true);
                        }
                    });
//aggiugno i test solo se non sono già presenti nella classe figlia.
                    if (!toRemove.get()) {
                        SootMethod n = new SootMethod(sootMethod.getName(), sootMethod.getParameterTypes(), sootMethod.getReturnType(), sootMethod.getModifiers());
                        Body b = (Body) sootMethod.getActiveBody().clone();


                        n.setActiveBody(b);

                        //Todo: forse da eliminare
                        n.setExceptions(sootMethod.getExceptions());
                        n.setPhantom(sootMethod.isPhantom());
                        n.setNumber(sootMethod.getNumber());
                        n.setSource(sootMethod.getSource());
                        //

                        n.setDeclared(false);

                        s.addMethod(n);
                        n.retrieveActiveBody();

                        n.setDeclared(true);

                    }
                    /*}catch (RuntimeException e)
                    {
                        e.printStackTrace();
                        cls.addMethod(sootMethod);
                        sootMethod.setDeclared(true);
                        sootMethod.setDeclaringClass(cls);
                    }*/

                }
            });
            //rimuovi la foglia dalle classi da analizzare ancora
            appClass.remove(s);
        }

    }


   /* public void moveToAnotherPackage(ArrayList<SootMethodMoved> moved){
        for(SootMethodMoved methodMoved : moved){
            for(SootClass prevClass : this.getProjectClasses()){
                if(prevClass.getName().equals(methodMoved.getOriginalClass().getName())){
                    for(SootMethod toMove : new ArrayList<>(prevClass.getMethods())){
                        if(toMove.getSubSignature().equals(methodMoved.getMethodMoved().getSubSignature())){
                           for(SootClass inToMove : this.getProjectClasses()){
                               // Todo: getmethodMoved.getMethodMoved().getDeclaringClass() restituisce cmq la classe original e non la classe nuova in cui è stato spsotato il metodo -> aggiungere collezione in                                          SootMethodMoved che contenga le classi in cui il metodo è stato spostato. Aggiungere controllo qui se effettivamete la classe in cui staremmo spostando il metodo in p è una classe figlia della stessa classe madre di p1 -> cioè della classe originale in cui era il metodo. Aggiungeere controllo per assicurarsi che in p non stiamo aggiugengendo un metodo giò presente nella classe in cui vorremmo spostare il metodo.

                               if(inToMove.getName().equals(methodMoved.getMethodMoved().getDeclaringClass().getName())){
                                   SootClass cls = toMove.getDeclaringClass();
                                   cls.removeMethod(toMove);
                                   toMove.setDeclared(false);
                                   toMove.setDeclaringClass(inToMove);
                                   inToMove.addMethod(toMove);
                                   toMove.setDeclared(true);
                               }
                           }
                        }
                    }
                }
            }

        }
    }
*/
}
