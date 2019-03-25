package testSelector.project;

import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.callgraph.CallGraph;
import testSelector.util.Util;
import testselector.exception.NoTestFoundedException;

import javax.annotation.Nonnull;
import java.nio.file.NotDirectoryException;
import java.util.*;

public class NewProject extends Project {
    private ArrayList<SootMethodMoved> movedToAnotherPackage;

    public NewProject(int junitVersion, String[] classPath, @Nonnull String... target) throws NoTestFoundedException, NotDirectoryException {
        this(junitVersion, classPath,null, target);

    }

    public NewProject(int junitVersion, String[] classPath, String[] toExclude, @Nonnull String... target) throws NoTestFoundedException, NotDirectoryException {
        super(junitVersion, classPath, toExclude, target);
        this.movedToAnotherPackage = new ArrayList<>();
        hierarchy = Scene.v().getActiveHierarchy();

    }

    public ArrayList<SootMethodMoved> getMovedToAnotherPackage() {
        return movedToAnotherPackage;
    }

    /*
     * Set all test-methods of the project as entry point for soot.
     */
    private void createEntryPoints() throws NoTestFoundedException {

        HashSet<SootMethod> allTesting;
        HashSet<SootClass> appClass = new HashSet<>(getProjectClasses());
        //    ArrayList<SootMethod> alreadyIn = new ArrayList<>();
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
                    //     if (alreadyIn.contains(m1))
                    //         continue;
                    //per tutti i test già aggiunti
                    for (SootMethod m : new HashSet<>(allTesting)) {
                        //se il metodo nella suprclasse è uguale ad un metodo della foglia (o di una classe sotto nella gerachia)
                        //non aggiungerlo
                        if (m.getSubSignature().equals(m1.getSubSignature()))
                            isIn = true;
                    }
                    if (!isIn) {
                        //aggiungi il test ereditato
                        allTesting.add(m1);

                    }
                }
            }




            allTesting.forEach(sootMethod -> {
                if(!sootMethod.getDeclaringClass().equals(s)){

                    movedToAnotherPackage.add(new SootMethodMoved(sootMethod, sootMethod.getDeclaringClass()));

                    SootClass cls = sootMethod.getDeclaringClass();
                    cls.removeMethod(sootMethod);
                    sootMethod.setDeclared(false);
                    sootMethod.setDeclaringClass(s);
                    s.addMethod(sootMethod);
                    sootMethod.setDeclared(true);

                }
            });






            //rimuovi la foglia dalle classi da analizzare ancora
            appClass.remove(s);
            //crea un test metodo fake che contiente tutti i metodi di test della gerarchia
            SootMethod entry = createTestMethod(allTesting, id, s);
            if (entry != null)
                //settalo come entrypoints per il callgraph
                getEntryPoints().add(entry);

            //  alreadyIn.addAll(allTesting);
        }

        Scene.v().setEntryPoints(new ArrayList<>(getEntryPoints()));
    }

    private SootMethod createTestMethod(HashSet<SootMethod> allTesting, int idMethodAndClass, SootClass leaf) {
        SootMethod method = new SootMethod("testMethodForTestClass" + idMethodAndClass,
                null,
                VoidType.v(), Modifier.PUBLIC);

        SootClass sc = new SootClass("testClass" + idMethodAndClass);
        SootMethod toWriteAsLast = null;

        for (SootMethod test : allTesting) {
            if (Util.isTearDown(test, getJunitVersion())) {
                toWriteAsLast = test;
                continue;
            }
            if (Util.isATestMethod(test, getJunitVersion())) {
                Local testTypeLocal = new JimpleLocal("try",RefType.v(leaf.getName()));
                JimpleBody body;
                try {
                    body = (JimpleBody) method.retrieveActiveBody();
                } catch (RuntimeException e) {
                    body = Jimple.v().newBody(method);
                    body.getLocals().add(testTypeLocal);
                    body.getUnits().add(Jimple.v().newAssignStmt(testTypeLocal, new JNewExpr(RefType.v(leaf.getName()))));
                    body.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(testTypeLocal, Scene.v().makeConstructorRef(Scene.v().getSootClass(leaf.getName()),null ))));

                }
                InvokeExpr invoke;
                if (!test.isStatic())
                    invoke = Jimple.v().newSpecialInvokeExpr(testTypeLocal, test.makeRef());
                else
                    invoke = Jimple.v().newStaticInvokeExpr(test.makeRef());

                if (Util.isSetup(test, getJunitVersion())) {
                    try {
                        body.getUnits().insertAfter(Jimple.v().newInvokeStmt(invoke), body.getUnits().getSuccOf(body.getUnits().getFirst()));

                    } catch (NoSuchElementException e) {
                        body.getUnits().add(Jimple.v().newInvokeStmt(invoke));

                    }
                } else
                    body.getUnits().add(Jimple.v().newInvokeStmt(invoke));


                method.setActiveBody(body);


            }

        }

        if (toWriteAsLast != null) {
            JimpleBody body;
            Local testTypeLocal;
            try {
                body = (JimpleBody) method.retrieveActiveBody();
            } catch (RuntimeException e) {
                body = Jimple.v().newBody(method);
            }
            if( body.getLocals().size() == 0)

                testTypeLocal  = new JimpleLocal("try",RefType.v(leaf.getName()));
            else
                testTypeLocal = body.getLocals().getFirst();

            InvokeExpr invoke;
            if (!toWriteAsLast.isStatic())
                invoke = Jimple.v().newSpecialInvokeExpr(testTypeLocal, toWriteAsLast.makeRef());
            else
                invoke = Jimple.v().newStaticInvokeExpr(toWriteAsLast.makeRef());

            try {
                body.getUnits().insertAfter(Jimple.v().newInvokeStmt(invoke), body.getUnits().getLast());
            } catch (NoSuchElementException e) {
                body.getUnits().add(Jimple.v().newInvokeStmt(invoke));
            }

            method.setActiveBody(body);
        }

        sc.addMethod(method);
        method.setDeclared(true);
        sc.setApplicationClass();
        Scene.v().addClass(sc);


        return allTesting.isEmpty() ? null : method;
    }

    /*
     * Run spark transformation
     */
    public void createCallgraph() throws NoTestFoundedException {

        createEntryPoints();


        Transform preprocessingTransfrom = new Transform("wjtp.refresolve", new SceneTransformer() {
            @Override
            protected void internalTransform(String phaseName, Map options) {
                LOGGER.info("rta call graph building...");
                Transform sparkTranform = new Transform("cg.spark", null);
                PhaseOptions.v().setPhaseOption(sparkTranform, "enabled:true"); //enable spark transformation
                PhaseOptions.v().setPhaseOption(sparkTranform, "apponly:true");
                PhaseOptions.v().setPhaseOption(sparkTranform, "rta:true"); //enable rta mode for call-graph
                PhaseOptions.v().setPhaseOption(sparkTranform, "verbose:false");
                PhaseOptions.v().setPhaseOption(sparkTranform, "on-fly-cg:false"); //disable default call-graph construction mode (soot not permitted to use rta and on-fly-cg options together)
                PhaseOptions.v().setPhaseOption(sparkTranform, "force-gc:true"); //force call a System.cg() to increase tue available space on garbage collector

                //     Map<String, String> opt = PhaseOptions.v().getPhaseOptions(sparkTranform);
                //     sparkTransform(sparkTranform, opt);
                CallGraph c = Scene.v().getCallGraph(); //take the call-graph builded
                setCallGraph(c); //set the callgraph as call-graph of this project

            }
        });
        Pack wjpppack = PackManager.v().getPack("wjtp");
        wjpppack.add(preprocessingTransfrom);





        //build the spark call-graph with the option setted
        //get the option setted


        PackManager.v().runPacks();



    }
}
