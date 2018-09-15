package com.company;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import soot.*;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.util.Chain;
import soot.util.dot.DotGraph;
import soot.util.queue.QueueReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static soot.SootClass.SIGNATURES;

//TODO: Refattorizzare: capire quali metodi sono veramente utili.
//TODO: Refattorizzare: eliminare tutti i System.out.println e sostituirli con un LOG
//TODO: REfattorizzare: Non si occupa di "troppe cose" questa classe?

public class Project {
    private ArrayList<SootMethod> applicationMethod;
    private ArrayList<SootClass> projectClasses;
    private ArrayList<SootMethod> entryPoints;
    private CallGraph callGraph;
    private String path;

    /**
     * The Project's constructor load in soot all class that are in the path given as a parametrer,
     * after set all tests method present in project as entry point to produce a CallGraph.
     * ATTENTION: THE CLASSES TEST MUST BE PLACED IN A PACKAGE NAME THAT CONTAINS THE WORD "test"
     *            AND ALL TEST METHODS NAME NEEDS TO CONTAINS THE WORD "test".
     * @param path the path of the project
     */
    public Project(String path){
        this.path = path;
        projectClasses = new ArrayList<>();
        applicationMethod = new ArrayList<>();
        entryPoints = new ArrayList<>();

        //reset soot
        soot.G.reset();
        //set soot options
        setSootOptions();
        //load all project class in soot
        loadClassesAndSupport();
        //load all methods of this project
        setApplicationMethod();
        //load all class needed
        Scene.v().loadNecessaryClasses();
        Scene.v().loadBasicClasses();
        Scene.v().loadDynamicClasses();
        //set all test methoda in projecy as entry points
        setEntryPoints();
        //     spark();
        //run the pack and so the callgraph transformation
        runPacks();
    }

    /**
     * Loads the class of the project in soot
     */
    private void loadClassesAndSupport() {
        ArrayList<String> classToLoad = processClasses();
        for(String classPath : classToLoad){
            addProjectClass(loadClass(classPath));
        }


    }

    /**
     * Load class using soot method loadClassAndSupport
     * @param name the name in soot-format of the class to losd
     * @return the sootClass that rappresented the class loaded.
     */
    private SootClass loadClass(String name) {
        Scene.v().addBasicClass(name, SIGNATURES);
        SootClass c = Scene.v().loadClassAndSupport(name);
        c.setApplicationClass();
        return c;
    }


    /**
     * Set the option for soot.
     */
    private void setSootOptions() {
        List<String> argsList = new ArrayList<>();
        argsList.add("-verbose");
        argsList.add("-w"); // whole program mode
        argsList.add("-cp"); // Soot class-path
        argsList.add(path + ";C:\\soot\\soot-3.1.0-jar-with-dependencies.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\jce.jar;C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2018.1.5\\lib\\junit-4.12.jar;C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2018.1.5\\lib\\hamcrest-core-1.3.jar;");
        Options.v().parse(argsList.toArray(new String[0]));

  }

   /*
    private void runPacks() {
        soot.PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {

            @Override
            protected void internalTransform(String phaseName, Map options) {

                CallGraph callGraph = Scene.v().getCallGraph();
                setCallGraph(callGraph);
                System.out.println("Serialize call graph start...");
                serializeCallGraph(callGraph, path + "//" + "-call-grsph" + DotGraph.DOT_EXTENSION);
                System.out.println("...Serialize call graph completed");

            }

        }));
        System.out.println("run pack...");
        PackManager.v().runPacks();
        System.out.println("...pack runned");

    }

   private static void spark(){
       HashMap<String, String> opt = new HashMap<>();
       opt.put("enabled", "true");
       opt.put("rta", "true");
       opt.put("verbose","true");
       opt.put("propagator","worklist");
       opt.put("simple-edges-bidirectional","false");
       opt.put("on-fly-cg","false");
       opt.put("set-impl","double");
       opt.put("double-set-old","hybrid");
       opt.put("double-set-new","hybrid");
       SparkTransformer.v().transform("wjtp",opt);

   }
   */

    private void runPacks() {
        Transform sparkTranform = new Transform("cg.spark", null);

        PhaseOptions.v().setPhaseOption(sparkTranform, "enabled:true");
        PhaseOptions.v().setPhaseOption(sparkTranform, "rta:true");
        PhaseOptions.v().setPhaseOption(sparkTranform, "verbose:true");
        PhaseOptions.v().setPhaseOption(sparkTranform, "propagator:worklist");
        PhaseOptions.v().setPhaseOption(sparkTranform, "simple-edges-bidirectional:false");
        PhaseOptions.v().setPhaseOption(sparkTranform, "on-fly-cg:false");
        PhaseOptions.v().setPhaseOption(sparkTranform, "set-impl:double");
        PhaseOptions.v().setPhaseOption(sparkTranform, "double-set-old:hybrid");
        PhaseOptions.v().setPhaseOption(sparkTranform, "double-set-new:hybrid");
        PhaseOptions.v().setPhaseOption(sparkTranform, "all-reachable:true");
        PhaseOptions.v().setPhaseOption(sparkTranform, "pre-jimplify:false");


        Map opt = PhaseOptions.v().getPhaseOptions(sparkTranform);
        System.out.println("rta call graph building...");

        SparkTransformer.v().transform(sparkTranform.getPhaseName(), opt);
        System.out.println("...rta call graph builded");

        CallGraph c = Scene.v().getCallGraph();
        setCallGraph(c);
        System.out.println("Serialize call graph start...");
        serializeCallGraph(callGraph, path + "//" + "-call-grsph" + DotGraph.DOT_EXTENSION);
        System.out.println("...Serialize call graph completed");

    }



    private static File serializeCallGraph(CallGraph graph, String fileName) {
        DotGraph canvas = new DotGraph("call-graph");
        QueueReader<Edge> listener = graph.listener();
        while (listener.hasNext()) {
            Edge next = listener.next();
            MethodOrMethodContext src = next.getSrc();
            MethodOrMethodContext tgt = next.getTgt();
            String srcToString = src.toString();
            String tgtToString = tgt.toString();
            if ((!srcToString.startsWith("<junit.") && !srcToString.startsWith("<java.") && !srcToString.startsWith("<sun.") && !srcToString.startsWith("<org.") && !srcToString.startsWith("<com.") && !srcToString.startsWith("<jdk.") && !srcToString.startsWith("<javax.")) || (!tgtToString.startsWith("<junit.") && !tgtToString.startsWith("<java.") && !tgtToString.startsWith("<sun.") && !tgtToString.startsWith("<org.") && !tgtToString.startsWith("<com.") && !tgtToString.startsWith("<jdk.") && !tgtToString.startsWith("<javax."))) {
                canvas.drawNode(srcToString);
                canvas.drawNode(tgtToString);
                canvas.drawEdge(srcToString, tgtToString);
            }
        }
        canvas.plot(fileName);
        return new File(fileName);
    }


    public ArrayList<SootMethod> getApplicationMethod() {
        return applicationMethod;
    }

    public void setApplicationMethod(ArrayList<SootMethod> applicationMethod) {
        this.applicationMethod = applicationMethod;
    }

    private void setApplicationMethod() {
        for (SootClass projectClass : this.projectClasses) {
            this.applicationMethod.addAll(projectClass.getMethods());
        }


    }
        public CallGraph getCallGraph() {
        return callGraph;
    }

    public void setCallGraph(CallGraph callGraph) {
        this.callGraph = callGraph;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<SootClass> getProjectClasses() {
        return projectClasses;
    }

    public void setProjectClasses(List<SootClass> projectClasses) {
        this.projectClasses.addAll(projectClasses);
    }

    public ArrayList<SootMethod> getEntryPoints() {
        return entryPoints;
    }

    /**
     * Scan all the folders of the project and return the soot-format-name of the classes.
     * @return An ArrayList with the soot-format-name of the all classes in the project
     */
    private ArrayList<String> processClasses(){
       List<File> fileToAdd;
       fileToAdd = processDirectory(path);
       ArrayList<String> classToProcess = new ArrayList<>();
       for(File f : fileToAdd) {
           String fName = f.getName().replace(".class", "");
           String fPath = f.getAbsolutePath().replace("\\", "-");
           String[] fPackage = fPath.split("-");
           int i = fPackage.length - 2;
           classToProcess.add(fPackage[i].concat(".").concat(fName));
       }

       return classToProcess;
   }


   //TODO: Posso eliminarla?
   private void setProjectClasses(){
        List<SootClass> yetPresent = null;
        if(!Scene.v().getApplicationClasses().isEmpty()){
            yetPresent = new ArrayList<>(Scene.v().getApplicationClasses());
        }
        List<SootClass> all = new ArrayList<>(Scene.v().getApplicationClasses());

        if(yetPresent != null) {
            for (SootClass sc : all) {
                if (!yetPresent.contains(sc))
                    projectClasses.add(sc);
            }
        }else
            setProjectClasses(all);

    }

    public void addProjectClass(SootClass c){
        projectClasses.add(c);
    }


    @Override
    public boolean equals(Object o){
        Project p = (Project) o;
        boolean check = true;
        for(SootClass sc : this.getProjectClasses()) {
         if(!p.getProjectClasses().contains(sc))
             check = false;
        }
        return check;
    }


    /**
     * Scan all the folders of the project and retunr the class file of the project
     * @param projectPath the path of the project
     * @return a list that contains all classes of the project in file format
     */
    private List<File> processDirectory(String projectPath) {
        List<File> file = (List<File>) FileUtils.listFiles(new File(projectPath), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        ArrayList<File> classFile = new ArrayList<>();
        for(File f : file){
            if(FilenameUtils.getExtension(f.getAbsolutePath()).equals("class"))
                classFile.add(f);
        }
        return  classFile;
    }


    /**
     * Set all test-methods of the project as entry point for soot.
     */
    private void setEntryPoints() {
        System.out.println("setting all test methods as entry points...");
        List<SootMethod> entryPoints = new ArrayList<>();
        Chain<SootClass> appCLass = Scene.v().getApplicationClasses();
        for(SootClass s : appCLass){
            if(s.getPackageName().contains("test")){
                List<SootMethod> classMethods = s.getMethods();
                for(SootMethod cs : classMethods){
                    if(cs.getName().contains("test"))
                        entryPoints.add(cs);
                }
            }
        }
        this.entryPoints.addAll(entryPoints);
        Scene.v().setEntryPoints(entryPoints);
        System.out.println("...entry points setted");

    }



}






















