package sootTest;

import soot.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.util.dot.DotGraph;
import soot.util.queue.QueueReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CallGraphExample {


    public static File serializeCallGraph(CallGraph graph, String fileName) {
        DotGraph canvas = new DotGraph("call-graph");
        QueueReader<soot.jimple.toolkits.callgraph.Edge> listener = graph.listener();
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

    public static void main(String[] args) throws IOException {


        setSootOptions();
        System.out.println("loading necessary classes.. ");
        Scene.v().loadClassAndSupport("sootTest.sootexample");
        Scene.v().loadClassAndSupport("test.test.test.sootexampleTest");
        Scene.v().loadNecessaryClasses();
        System.out.println("necessary classes loaded");
        setEntryPoints();

        soot.PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {

            @Override
            protected void internalTransform(String phaseName, Map options) {

                CHATransformer.v().transform();
                CallGraph callGraph = Scene.v().getCallGraph();
                System.out.println("Serialized call graph start..");
                serializeCallGraph(callGraph, "output-test" + DotGraph.DOT_EXTENSION);
                System.out.println("Serialized call graph completed");

            }

        }));
        System.out.println("run pack..");
        PackManager.v().runPacks();
        System.out.println("pack runned");


    }


    private static void setEntryPoints() {
        System.out.println("setting all test methods as entry points...");
        List<SootMethod> entryPoints = new ArrayList<SootMethod>();

        SootClass sootClass = Scene.v().getSootClass("test.test.test.sootexampleTest");
        List<SootMethod> classMethods = sootClass.getMethods();
        for (SootMethod cs : classMethods) {
            if (cs.getName().contains("test"))
                entryPoints.add(cs);
        }
        Scene.v().setEntryPoints(entryPoints);
        System.out.println("entry points setted");

    }

    private static void setSootOptions() {
        System.out.println("setting soot options...");
        List<String> argsList = new ArrayList<String>();
        argsList.add("-W"); // whole program mode
        argsList.add("-cp"); // Soot class-path
        argsList.add("C:\\Users\\Dario\\IdeaProjects\\test\\out\\production\\test;C:\\Users\\Dario\\Desktop\\soot-3.1.0-jar-with-dependencies.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\jce.jar;C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2018.1.5\\lib\\junit-4.12.jar;C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2018.1.5\\lib\\hamcrest-core-1.3.jar;");
        //argsList.add("-process-dir"); // Classes to analyze
        //argsList.add("C:\\Users\\Dario\\IdeaProjects\\test\\out\\production\\test");
        //argsList.add("-src-prec");
        //argsList.add("only-class");
        Options.v().parse(argsList.toArray(new String[0]));
        System.out.println("soot option setted");
    }

}
