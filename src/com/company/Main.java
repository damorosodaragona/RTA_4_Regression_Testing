package com.company;

import soot.*;

import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

import soot.util.Chain;
import soot.util.dot.DotGraph;
import soot.util.queue.QueueReader;

import java.io.File;
import java.util.*;

public class Main {
    private static String oldProject = new String();
    private static List<SootClass> oldProjectApplicationClassess = new ArrayList<>();
    private static List<SootClass> newProjectApplicationClassess = new ArrayList<>();


    public static void main(String[] args) {
        setSootOptions(args);
        if(args.length == 2)
           oldProject = args[1];
        System.out.println("loading necessary classes.. ");
        Scene.v().loadNecessaryClasses();
        System.out.println("necessary classes loaded");
        setEntryPoints();
        setSparkPointsToAnalysisAndTransform();
        CallGraph callGraph = Scene.v().getCallGraph();
        System.out.println("Serialized call graph start..");
        serializeCallGraph(callGraph, "spark_call_graph with junit" + DotGraph.DOT_EXTENSION);
        System.out.println("Serialized call graph completed");



    }



    private static void setSparkPointsToAnalysisAndTransform(){
        HashMap opt = new HashMap();
        opt.put("enabled","true");
        opt.put("verbose","true");
        opt.put("propagator","worklist"); //worklist
        opt.put("simple-edges-bidirectional","true");
        opt.put("on-fly-cg","false");
        opt.put("set-impl","double");
        opt.put("double-set-old","hybrid");
        opt.put("double-set-new","hybrid");
        opt.put("dump-html","false");
        opt.put("dump-pag","false");
        opt.put("string-constants","false");
        opt.put("rta", "true");
        /*opt.put("propagator","worklist");
        opt.put("simple-edges-bidirectional","true");
        opt.put("on-fly-cg","true");
        opt.put("set-impl","double");
        opt.put("pre-jimplify", "true");
        opt.put("string-constants", "true");
        opt.put("simulate-natives", "true");
        opt.put("dump-html", "true");
        opt.put("geom-pta", "true");
        opt.put("dump-pag", "true");
        opt.put("topo-sort", "true");
        opt.put("dump-types", "true");
        opt.put("class-method-var", "true");
        opt.put("dump-answer", "true");
        opt.put("double-set-old","hybrid");
        opt.put("double-set-new","hybrid");*/
        SparkTransformer.v().transform("", opt);

    }

    private static void setSootOptions(String []args) {
        System.out.println("setting soot options...");
        List<String> argsList = new ArrayList<String>();
        argsList.add("-W"); // whole program mode
        argsList.add("-cp"); // Soot class-path
        argsList.add("C:\\soot\\soot-3.1.0-jar-with-dependencies.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\jce.jar;C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2018.1.5\\lib\\junit-4.12.jar;C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2018.1.5\\lib\\hamcrest-core-1.3.jar;");
        argsList.add("-process-dir"); // Classes to analyze
        argsList.add(args[0]);
        Options.v().parse(argsList.toArray(new String[0]));
        System.out.println("soot option setted");
    }

    private static void setEntryPoints() {
        System.out.println("setting all test methods as entry points...");
        List<SootMethod> entryPoints = new ArrayList<>();
        Chain<SootClass> sootClasses =  Scene.v().getApplicationClasses();
        Iterator<SootClass> it =  sootClasses.iterator();
        while(it.hasNext()) {
            SootClass sootClass = it.next();
            if (sootClass.getJavaPackageName().contains("test")) {
                entryPoints.addAll(sootClass.getMethods());
            }
        }

        Scene.v().setEntryPoints(entryPoints);
        System.out.println("entry points setted");

    }

    public static File serializeCallGraph(CallGraph graph, String fileName) {
        DotGraph canvas = new DotGraph("call-graph");
        QueueReader<Edge> listener = graph.listener();
        while (listener.hasNext()) {
            Edge next = listener.next();
            MethodOrMethodContext src = next.getSrc();
            MethodOrMethodContext tgt = next.getTgt();
            String srcToString = src.toString();
            String tgtToString = tgt.toString();
            if ((!srcToString.startsWith("<java.") && !srcToString.startsWith("<sun.") && !srcToString.startsWith("<org.") && !srcToString.startsWith("<com.") && !srcToString.startsWith("<jdk.") && !srcToString.startsWith("<javax.")) || (!tgtToString.startsWith("<junit.") && !tgtToString.startsWith("<java.") && !tgtToString.startsWith("<sun.") && !tgtToString.startsWith("<org.") && !tgtToString.startsWith("<com.") && !tgtToString.startsWith("<jdk.") && !tgtToString.startsWith("<javax."))) {
                canvas.drawNode(srcToString);
                canvas.drawNode(tgtToString);
                canvas.drawEdge(srcToString, tgtToString);
            }
            if(srcToString.contains("junit")){
                canvas.drawNode(srcToString);
                canvas.drawNode(tgtToString);
                canvas.drawEdge(srcToString, tgtToString);
            }
        }
        canvas.plot(fileName);
        return new File(fileName);
    }





}
