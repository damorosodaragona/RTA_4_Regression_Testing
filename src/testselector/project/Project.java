package testselector.project;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import soot.*;
import soot.baf.Baf;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.util.dot.DotGraph;
import soot.util.queue.QueueReader;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;
import testselector.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.NotDirectoryException;
import java.util.*;

//TODO: REfattorizzare: Non si occupa di "troppe cose" questa classe?

public class Project {
    private ArrayList<SootMethod> applicationMethod;
    private final HashSet<SootClass> projectClasses;
    private HashSet<SootMethod> entryPoints;
    private CallGraph callGraph;
    private ArrayList<String> target;

    public ArrayList<String> getClassPath() {
        return classPath;
    }

    private ArrayList<String> classPath;

    public int getJunitVersion() {
        return junitVersion;
    }

    private int junitVersion;

    public Map<SootClass, ArrayList<SootMethod>> getTestingClass() {
        return testingClass;
    }

    private Map<SootClass, ArrayList<SootMethod>> testingClass;
    private static final Logger LOGGER = Logger.getLogger(Main.class);
    @Nullable
    private Object o;

    /**
     * The Project's constructor load in soot all class that are in the paths given as a parametrer,
     * after set all tests method present in project as entry point to produce a CallGraph.
     *
     * @param callgraph
     * @param junitVersion
     * @param classPath
     * @param target       the paths of the classes module
     */
    public Project(boolean callgraph, int junitVersion, String[] classPath, @Nonnull String... target) throws NoTestFoundedException, NotDirectoryException {

        //validate the project paths
        validatePaths(target);

        this.classPath = new ArrayList<>();
        this.target = new ArrayList<>();
        this.projectClasses = new HashSet<>();
        this.applicationMethod = new ArrayList<>();
        this.entryPoints = new HashSet<>();
        this.testingClass = new HashMap<>();
        this.junitVersion = junitVersion;
        setTarget(target);

        setClassPath(classPath);

        //reset soot
        soot.G.reset();

        //set soot options
        setSootOptions();
        Scene.v().loadNecessaryClasses();
        setApplicationClass();

        if(callgraph) {
            createEntryPoints();
            createCallgraph();
        }
        PackManager.v().runPacks();


        //load all project class in soot
        //   loadClassesAndSupport();

        //load all class needed
        LOGGER.info("Soot loading necessary classes");
        // Scene.v().loadBasicClasses();
        // Scene.v().loadDynamicClasses();
        //add all classes to this project classes

        //load all methods of this project
        setApplicationMethod();


        //set all test methoda in projecy as entry points
        //createEntryPoints();
        if(!callgraph)
            Scene.v().releaseActiveHierarchy();
    }


    /*
     * Popolate <code>paths</code> ArrayList with the passed string path.
     *
     * @param classPath
     */
    private void setClassPath(String[] classPath) {
        for (int i = 0; i < classPath.length; i++) {
            this.classPath.add(classPath[i]);
        }
    }

    /*
     * Check if the paths passed are valid directories or not
     *
     * @param modulePath the project paths
     * @throws NotDirectoryException if the paths passed are not valid directories
     */
    private void validatePaths(@Nonnull String[] modulePath) throws NotDirectoryException {
        //are the parameter paths valid?
        for (int i = 0; i < modulePath.length; i++) {
            File f = new File(modulePath[i]);
            if (!f.isDirectory())
                throw new NotDirectoryException(f.getAbsolutePath());
        }
    }

    /*
     * Popolate <code>paths</code> ArrayList with the passed string path.
     *
     * @param target
     */
    private void setTarget(@Nonnull String[] target) {
        for (int i = 0; i < target.length; i++) {
            this.target.add(target[i]);
        }
    }

    /*
     * Add the application classes loaded in soot in <code>projectClasses</code> ArrayList
     */
    private void setApplicationClass() {
        projectClasses.addAll(Scene.v().getApplicationClasses());
    }


    /*
     * Loads the class of the project in soot
     */
    private void loadClassesAndSupport() {
        List<String> classToLoad = processClasses();
        for (String classPath : classToLoad) {
            //add all classes founded in the passed directory first in SootScene as application class and then in projectClass ArrayList
            loadClass(classPath);
        }
    }


    /*
     * Load class using soot method loadClassAndSupport
     *
     * @param name the name in soot-format of the class to losd
     * @return the sootClass that rappresented the class loaded.
     */
    private SootClass loadClass(@Nonnull String name) {
        //Load class in Soot Scene with SIGNATURE level
        SootClass c = Scene.v().loadClassAndSupport(name);
        //set the Soot Class as application class
        c.setApplicationClass();
        //return the class loaded
        return c;
    }

    /**
     * Set the option for soot.
     */
    private void setSootOptions() {
        List<String> argsList = new ArrayList<>();
        argsList.add("-w"); // whole program mode
        argsList.add("-O");
        argsList.add("-no-bodies-for-excluded"); //don't load bodies for excluded classes, so for non-application-classes
        argsList.add("-allow-phantom-refs"); // allow to don't load some classes (it's necessary for "no-bodies-for-excluded" option)
       // argsList.add("-src-prec");
       // argsList.add("java");
        argsList.add("-f");
        argsList.add("jimple");
        //   argsList.add("dava");
        argsList.add("-p");
        argsList.add("jb.lns");
        argsList.add("sort-locals:true");
        argsList.add("-cp");// Soot class-paths

        //add all modules path to Soot class-paths
        String s = new String();
        for (int i = 0; i < classPath.size(); i++) {
            s += classPath.get(i) + ";";
        }
        argsList.add(s);

        //"C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\jce.jar;C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar"

        //set all modules path as directories to process
        for (int i = 0; i < target.size(); i++) {
            argsList.add("-process-dir");
            argsList.add(target.get(i));
        }

        //parse the option
        //      PhaseOptions.v().setPhaseOption("jb.tr", "enabled:false");

        Options.v().parse(argsList.toArray(new String[0]));


    }
    //  https://www.spankingtube.com/video/72545/ok-boss-i-m-ready-to-be-strapped-the-extended-cut

    /*
     * Run spark transformation
     */
    public void createCallgraph() {

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






    }

    private void sparkTransform(Transform sparkTranform, Map<String, String> opt) {
        SparkTransformer.v().transform(sparkTranform.getPhaseName(), opt);

    }

    private void bodyTransform() {
//
//       Transform preprocessingTransfrom = new Transform("wjpp.refresolve", new SceneTransformer() {
//            @Override
//            protected void internalTransform(String phaseName, Map options) {
//
////                     p.add(new Transform("jb.tt", soot.toolkits.exceptions.TrapTightener.v()));
////                             p.add(new Transform("jb.ls", LocalSplitter.v()));
////                             p.add(new Transform("jb.a", Aggregator.v()));
////                             p.add(new Transform("jb.ule", UnusedLocalEliminator.v()));
////                             p.add(new Transform("jb.tr", TypeAssigner.v()));
////                             p.add(new Transform("jb.ulp", LocalPacker.v()));
////                             p.add(new Transform("jb.lns", LocalNameStandardizer.v()));
////                             p.add(new Transform("jb.cp", CopyPropagator.v()));
////                             p.add(new Transform("jb.dae", DeadAssignmentEliminator.v()));
////                             p.add(new Transform("jb.cp-ule", UnusedLocalEliminator.v()));
////                             p.add(new Transform("jb.lp", LocalPacker.v()));
////                             p.add(new Transform("jb.ne", NopEliminator.v()));
////                             p.add(new Transform("jb.uce", UnreachableCodeEliminator.v()));
//
//            }
//        });

//        soot.Transform inlineTransform = new soot.Transform("jb.rfinline", new
//                BodyTransformer() {
//                    @Override
//                    protected void internalTransform(Body b, String phaseName, Map options) {
//                        //perform some Body Transformations here using the results of the first
//
//                    }
//                });
        //add the Transformer to the wjpp phase
        //Pack wjpppack = PackManager.v().getPack("wjpp");
     //   wjpppack.add(preprocessingTransfrom);
        //add the body transformer to the jtp phase
      //  PackManager.v().getPack("jtp").add(inlineTransform);
    //    PhaseOptions.v().setPhaseOption("db", "enabled:true");
       PackManager.v().getPack("bb").add(new Transform("bb.mytrans", new BodyTransformer() {
           @Override
           protected void internalTransform(Body body, String s, Map<String, String> map) {

               body.getMethod().setActiveBody(Baf.v().newBody(body.getMethod()));
           }
       })
        );
//        PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {
//
//            @Override
//            protected void internalTransform(String phaseName, Map options) {
//                CHATransformer.v().transform();
//            }
//
//        }));
       // PackManager.v().getPack("jb").apply();
    }

    /**
     * Save the generated call graph in .dot format. To get a claer callgraph all java,sun,org,jdk,javax methods and calls in the saved callgraph not appear
     *
     * @param path a string that represent the path where save the callgraph
     * @param name the name with wich save the callgraph
     * @throws NoPathException if the path passed is empty or null
     * @throws NoNameException if the name passed is empty or null
     */
    public void saveCallGraph(String path, String name) throws NoPathException, NoNameException {
        if (path == null || path.isEmpty())
            throw new NoPathException();
        if (name == null || name.isEmpty())
            throw new NoNameException();
        LOGGER.info("Serialize call graph...");
        DotGraph canvas = new DotGraph(name + "-call-graph");
        QueueReader<Edge> listener = this.getCallGraph().listener();
        while (listener.hasNext()) {
            Edge next = listener.next();
            MethodOrMethodContext src = next.getSrc();
            MethodOrMethodContext tgt = next.getTgt();
            String srcToString = src.toString();
            String tgtToString = tgt.toString();
            if ((!srcToString.startsWith("<sun.") && !srcToString.startsWith("<org.") && !srcToString.startsWith("<jdk.") && !srcToString.startsWith("<javax.")) || (!tgtToString.startsWith("<java.") && !tgtToString.startsWith("<sun.") && !tgtToString.startsWith("<org.") && !tgtToString.startsWith("<jdk.") && !tgtToString.startsWith("<javax."))) {
                canvas.drawNode(srcToString);
                canvas.drawNode(tgtToString);
                canvas.drawEdge(srcToString, tgtToString);
            }
        }


        canvas.plot(path + File.separator + File.separator + name + "-call-graph" + DotGraph.DOT_EXTENSION);
        new File(path);
    }

    /**
     * Get all methods in this project.
     *
     * @return a {@link soot.SootMethod} list with all methods in this project.
     */
    public List<SootMethod> getApplicationMethod() {
        return applicationMethod;
    }

    private void setApplicationMethod() {
        for (SootClass projectClass : this.projectClasses) {
            this.applicationMethod.addAll(projectClass.getMethods());
        }
    }

    /**
     * Get the {@link CallGraph} generated for this project
     *
     * @return a {@link CallGraph} object that represent the callgraph generated for this project
     */
    public CallGraph getCallGraph() {
        return callGraph;
    }

    /**
     * Set the {@link CallGraph} for this project
     *
     * @param callGraph the {@link CallGraph} to set for this project
     */
    public void setCallGraph(CallGraph callGraph) {
        this.callGraph = callGraph;
    }

    /**
     * Get the target setted for this project
     *
     * @return a String List with the path of the modules setted for this project
     */
    public List<String> getTarget() {
        return target;
    }

    /**
     * Get the all classes in this project
     *
     * @return a {@link SootClass} List with the path of the modules setted for this project    )
     */
    public HashSet<SootClass> getProjectClasses() {

        return new HashSet<>(projectClasses);
    }

    /**
     * Get the entry points for this project. The entry points in this case are the tests methods present in this project, so tha {@link CallGraph} start from this entry points.
     *
     * @return a  {@link SootMethod} List which contains the entry points for this project
     */
    public HashSet<SootMethod> getEntryPoints() {
        return entryPoints;
    }

    /*
     * Scan all the folders of the project and return the soot-format-name of the classes.
     *
     * @return An ArrayList with the soot-format-name of the all classes in the project
     */
    private List<String> processClasses() {
        List<File> fileToAdd;
        fileToAdd = processDirectory();
        List<String> classToProcess = new ArrayList<>();
        for (File f : fileToAdd) {
            String fName = f.getName().replace(".class", "");
            String fPath = f.getAbsolutePath().replace("\\", "-");
            String[] fPackage = fPath.split("-");
            int i = fPackage.length - 2;
            classToProcess.add(fPackage[i].concat(".").concat(fName));
        }

        return classToProcess;
    }

    /**
     * Get the hashcode for this project calculated with the method {@link Objects}.hash().
     *
     * @return a int hashcode for this project.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getProjectClasses());
    }

    /**
     * Check if two project are equal.
     *
     * @param o the project to confront
     * @return true only if the two project contains the same classes
     */
    @Override
    public boolean equals(@Nullable Object o) {
        this.o = o;
        if (o == null)
            return false;


        if (o.getClass() != this.getClass())
            return false;

        Project p = (Project) o;
        boolean check = true;
        for (SootClass sc : this.getProjectClasses()) {
            if (!p.getProjectClasses().contains(sc))
                check = false;
        }
        for (SootClass sc : p.getProjectClasses()) {
            if (!this.getProjectClasses().contains(sc))
                check = false;
        }
        return check;
    }


    /*
     * Scan all the folders of the project and retunr the class file of the project
     *
     * @return a list that contains all classes of the project in file format
     */
    private List<File> processDirectory() {
        ArrayList<File> classFile = new ArrayList<>();
        //for each modules path
        for (String path : target) {
            //get a list of file
            List<File> file = (List<File>) FileUtils.listFiles(new File(path), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            //for each file
            for (File f : file) {
                //if the file is .class
                if ("class".equals(FilenameUtils.getExtension(f.getAbsolutePath())))
                    //add file
                    classFile.add(f);
            }
        }
        //return the class file of the project
        return classFile;
    }


    /*
     * Set all test-methods of the project as entry point for soot.
     */
    private void createEntryPoints() throws NoTestFoundedException {

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
            //aggiungere controllo se sono metodi junit prima di aggiungerli

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
                        if (m.getSubSignature().equals(m1.getSubSignature()))
                            isIn = true;
                    }
                    if (!isIn) {
                        //aggiungi il test ereditato
                        allTesting.add(m1);
                    }
                }
            }

           /* allTesting.forEach(sootMethod -> {
                if(!sootMethod.getDeclaringClass().equals(s)){
                    SootClass cls = sootMethod.getDeclaringClass();
                    cls.removeMethod(sootMethod);
                    sootMethod.setDeclared(false);
                    sootMethod.setDeclaringClass(s);
                    s.addMethod(sootMethod);
                    sootMethod.setDeclared(true);
                }
            });*/

            //rimuovi la foglia dalle classi da analizzare ancora
            appClass.remove(s);
         //   appClass.removeAll(superClasses);
            //crea un test metodo fake che contiente tutti i metodi di test della gerarchia
            SootMethod entry = createTestMethod(allTesting, id, s);
            if (entry != null)
                //settalo come entrypoints per il callgraph
                entryPoints.add(entry);

        }
        Scene.v().setEntryPoints(new ArrayList<>(entryPoints));

//            if (testingClass.containsKey(s))
//                testingClass.get(s).add(sootMethod);
//            else {
//                ArrayList<SootMethod> sm = new ArrayList<>();
//                sm.add(sootMethod);
//                testingClass.put(s, sm);
//            }
    }

/*private void createEntryPoints(){

    for(SootClass sc : this.getProjectClasses()){
        List<SootMethod> methods = sc.getMethods();
        for(SootMethod sm : methods){
            if(Util.isJunitTestCase(sm, getJunitVersion()) && !Modifier.isAbstract(sm.getModifiers())){
               entryPoints.add(sm);
            }
        }
        Scene.v().setEntryPoints(new ArrayList<>(entryPoints));
    }
}*/
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
            if (Util.isATestMethod(test, junitVersion)) {
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

                InvokeExpr invoke = Jimple.v().newSpecialInvokeExpr(testTypeLocal, test.makeRef());

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
            try {
                body = (JimpleBody) method.retrieveActiveBody();
            } catch (RuntimeException e) {
                body = Jimple.v().newBody(method);
            }

            Local testTypeLocal = body.getLocals().getFirst();
            InvokeExpr invoke = Jimple.v().newSpecialInvokeExpr(testTypeLocal, toWriteAsLast.makeRef());

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

    public void removeEntryPoint(SootMethod entryPoints) {
        this.entryPoints.remove(entryPoints);
    }
}


