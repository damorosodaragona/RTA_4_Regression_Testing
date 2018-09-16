package com.company;

public class newMain {


    public static void main(String[] args) {
        Project oldProject = new Project(args[0]);
        Project newProject = new Project(args[1]);
        TestSelector u = new TestSelector(oldProject, newProject);
        u.selectTest();
    }

    /*

    public static File serializeCallGraph(CallGraph graph, String fileName) {
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



    private static ArrayList findSameMethodsWithChange(Project oldProject, Project newProject) {
        ArrayList<SootMethod> methodWithChange = new ArrayList<>();
        for (SootClass newProjectClasses : newProject.getProjectClasses()) {
            for (SootClass oldProjectClasses : oldProject.getProjectClasses()) {
                if (newProjectClasses.getName().equals(oldProjectClasses.getName())) {
                    List<SootMethod> newProjectClassMethods = newProjectClasses.getMethods();
                    List<SootMethod> oldProjectClassMethods = oldProjectClasses.getMethods();
                    for (SootMethod newProjectMethod : newProjectClassMethods) {
                        for (SootMethod oldProjectMethod : oldProjectClassMethods) {
                            if (newProjectMethod.getName().equals(oldProjectMethod.getName()))
                                if (newProjectMethod.equivHashCode() != oldProjectMethod.equivHashCode()) {
                                    System.out.println(newProjectMethod.getName());
                                    methodWithChange.add(newProjectMethod);
                                }

                        }
                    }


                }
            }
        }
        return methodWithChange;
    }

    private static ArrayList findSameMethodsWithChange() {
        List<SootClass> allApplicationCalsses = new ArrayList<>(Scene.v().getApplicationClasses());
        ArrayList<SootMethod> diiferentMethods = new ArrayList<>();
        for (SootClass sc : allApplicationCalsses) {
            for(SootClass sc1 : allApplicationCalsses) {

                if (sc.getJavaStyleName().equals(sc1.getJavaStyleName())) {
                    List<SootMethod> firstClassmethods = sc1.getMethods();
                    List<SootMethod> lastClassMethods = sc.getMethods();
                    for (SootMethod sm : firstClassmethods) {
                        for (SootMethod sm1 : lastClassMethods) {
                            if (sm.getName().equals(sm1.getName()))
                                if (sm.equivHashCode() != sm1.equivHashCode()) {
                                    System.out.println("diiferent methods sm name" + sm.getName());
                                    diiferentMethods.add(sm);
                                    System.out.println("diiferent methods sm canonocal name" + sm.getClass().getCanonicalName());
                                    System.out.println("diiferent methods sm simple name" + sm.getClass().getSimpleName());
                                    System.out.println("diiferent methods sm1 canonical name" + sm1.getClass().getCanonicalName());
                                    System.out.println("diiferent methods sm1 simple name" + sm1.getClass().getSimpleName());
                                }
                        }
                    }
                }
            }
        }
        return diiferentMethods;
    }
    */
}
