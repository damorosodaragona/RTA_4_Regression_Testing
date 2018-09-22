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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static soot.SootClass.*;

//TODO: REfattorizzare: Non si occupa di "troppe cose" questa classe?

public class Project {
    private ArrayList<SootMethod> applicationMethod;
    private ArrayList<SootClass> projectClasses;
    private ArrayList<SootMethod> entryPoints;
    private CallGraph callGraph;
    private ArrayList<String> paths;
    private static final Logger LOGGER = Logger.getLogger(Project.class.getName());

    /**
     * The Project's constructor load in soot all class that are in the paths given as a parametrer,
     * after set all tests method present in project as entry point to produce a CallGraph.
     * ATTENTION: THE CLASSES TEST MUST BE PLACED IN A PACKAGE NAME THAT CONTAINS THE WORD "test"
     * AND ALL TEST METHODS NAME NEEDS TO CONTAINS THE WORD "test".
     *
     * @param modulePath the paths of the project
     */
    public Project(@Nonnull String... modulePath) {
        //     LOGGER.info("Loading project:" + path);

        paths = new ArrayList<>();
        projectClasses = new ArrayList<>();
        applicationMethod = new ArrayList<>();
        entryPoints = new ArrayList<>();

        setPaths(modulePath);

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
//        tryToLoad();

        Scene.v().loadBasicClasses();
        Scene.v().loadDynamicClasses();
        setApplicationClass();

        //set all test methoda in projecy as entry points
        setEntryPoints();
        //run the pack and so the callgraph transformation
        runPacks();


    }

    private void setPaths(@Nonnull String[] modulePath) {
        for (int i = 0; i < modulePath.length; i++) {
            this.paths.add(modulePath[i]);
        }
    }

    private void setApplicationClass() {
        projectClasses.addAll(Scene.v().getApplicationClasses());
    }


    /**
     * Loads the class of the project in soot
     */
    private void loadClassesAndSupport() {
        ArrayList<String> classToLoad = processClasses();
        for (String classPath : classToLoad) {
            addProjectClass(loadClass(classPath));
        }
    }


    private void tryToLoad() {
        try {
            Scene.v().loadNecessaryClasses();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Class names not equal! ")) {
                String message = e.getMessage().replace("Class names not equal! ", "");
                String[] classes = message.split(" != ");
                String classNameToReload = classes[1];
                String classNameToDelete = classes[0];
                SootClass toRemove = null;
                for (SootClass sc : projectClasses) {
                    if (sc.getName().equals(classNameToDelete)) {
                        toRemove = sc;
                        Scene.v().removeClass(toRemove);

                    }
                }
                projectClasses.remove(toRemove);
                addProjectClass(loadClass(classNameToReload));
                tryToLoad();
            } else
                e.printStackTrace();
        }
    }

    /**
     * Load class using soot method loadClassAndSupport
     *
     * @param name the name in soot-format of the class to losd
     * @return the sootClass that rappresented the class loaded.
     */
    private SootClass loadClass(@Nonnull String name) {
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
        argsList.add("-W"); // whole program mode
        argsList.add("-no-bodies-for-excluded");
        argsList.add("-allow-phantom-refs");
        argsList.add("-cp"); // Soot class-paths

        for (int i = 0; i < paths.size(); i++) {
            argsList.add(paths.get(i));
        }

        for (int i = 0; i < paths.size(); i++) {
            argsList.add("-process-dir");
            argsList.add(paths.get(i));
        }

        Options.v().parse(argsList.toArray(new String[0]));
        //soot.Main.main(argsList.toArray(new String[0]));

    }
    //  https://www.spankingtube.com/video/72545/ok-boss-i-m-ready-to-be-strapped-the-extended-cut

    private void runPacks() {
        Transform sparkTranform = new Transform("cg.spark", null);

        PhaseOptions.v().setPhaseOption(sparkTranform, "enabled:true");
        PhaseOptions.v().setPhaseOption(sparkTranform, "rta:true");
        PhaseOptions.v().setPhaseOption(sparkTranform, "verbose:true");
        PhaseOptions.v().setPhaseOption(sparkTranform, "on-fly-cg:false");
        PhaseOptions.v().setPhaseOption(sparkTranform, "force-gc:true");
        PhaseOptions.v().setPhaseOption(sparkTranform, "apponly:true");


        Map<String, String> opt = PhaseOptions.v().getPhaseOptions(sparkTranform);

        LOGGER.info("rta call graph building...");
        sparkTransform(sparkTranform, opt);

        //   LOGGER.info("...rta call graph builded");
        CallGraph c = Scene.v().getCallGraph();
        setCallGraph(c);
        LOGGER.info("Serialize call graph...");
        String ps[] = paths.get(0).split(File.separator + File.separator);
        String toElimi = ps[ps.length - 1];
        String path = paths.get(0).replace(toElimi + File.separator, "");
        serializeCallGraph(path + "-call-grsph" + DotGraph.DOT_EXTENSION);
        // LOGGER.info("...Serialize call graph completed");

    }

    private void sparkTransform(Transform sparkTranform, Map<String, String> opt) {
        try {
            SparkTransformer.v().transform(sparkTranform.getPhaseName(), opt);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("This operation requires resolving level")) {
                LOGGER.log(Level.INFO, e.getMessage(), e);

                String[] s = e.getMessage().split(":");
                String[] s1 = s[1].split(";");
                String s2[] = s1[0].split("addBasicClass");
                String s3[] = s2[1].replace("(", "").replace(")", "").split(",");
                String clazz = s3[0];
                String level = s3[1];
                tryToLoadClass(clazz, level);
                sparkTransform(sparkTranform, opt);
            } else
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void tryToLoadClass(String clazz, String level) {
        try {

            if (level.equals("SIGNATURES")) {
                Scene.v().loadClass(clazz, SIGNATURES);
            } else if (level.equals(String.valueOf("BODIES"))) {
                Scene.v().loadClass(clazz, BODIES);

            } else {
                Scene.v().loadClass(clazz, HIERARCHY);

            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);

            String[] s = e.getMessage().split(":");
            String[] s1 = s[1].split(";");
            String s2[] = s1[0].split("addBasicClass");
            String s3[] = s2[1].replace("(", "").replace(")", "").split(",");
            String clazz_ = s3[0];
            String level_ = s3[1];
            tryToLoadClass(clazz_, level_);
            tryToLoadClass(clazz, level);
        }
    }


    private void serializeCallGraph(String fileName) {
        DotGraph canvas = new DotGraph("call-graph");
        QueueReader<Edge> listener = this.getCallGraph().listener();
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
        new File(fileName);
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

    public ArrayList<String> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<String> paths) {
        this.paths = paths;
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
     *
     * @return An ArrayList with the soot-format-name of the all classes in the project
     */
    private ArrayList<String> processClasses() {
        List<File> fileToAdd;
        fileToAdd = processDirectory(paths);
        ArrayList<String> classToProcess = new ArrayList<>();
        for (File f : fileToAdd) {
            String fName = f.getName().replace(".class", "");
            String fPath = f.getAbsolutePath().replace("\\", "-");
            String[] fPackage = fPath.split("-");
            int i = fPackage.length - 2;
            classToProcess.add(fPackage[i].concat(".").concat(fName));
        }

        return classToProcess;
    }


    public void addProjectClass(SootClass c) {
        projectClasses.add(c);
    }


    @Override
    public boolean equals(@Nullable Object o) {
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
        return check;
    }


    /**
     * Scan all the folders of the project and retunr the class file of the project
     *
     * @param projectPath the paths of the project
     * @return a list that contains all classes of the project in file format
     */
    private List<File> processDirectory(ArrayList<String> projectPath) {
        ArrayList<File> classFile = new ArrayList<>();

        for (String path : paths) {
            List<File> file = (List<File>) FileUtils.listFiles(new File(path), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            for (File f : file) {
                if (FilenameUtils.getExtension(f.getAbsolutePath()).equals("class"))
                    classFile.add(f);
            }
        }
        return classFile;
    }


    /**
     * Set all test-methods of the project as entry point for soot.
     */
    private void setEntryPoints() {

        LOGGER.info("setting all test methods as entry points...");
        List<SootMethod> entryPoints = new ArrayList<>();
        Chain<SootClass> appClass = Scene.v().getApplicationClasses();
        for (SootClass s : appClass) {
            List<SootMethod> classMethods = s.getMethods();
            for (SootMethod sootMethod : classMethods) {
                if (Util.isJunitTestCase(sootMethod)) {

                    entryPoints.add(sootMethod);

                }
            }


        }

        this.entryPoints.addAll(entryPoints);
        Scene.v().setEntryPoints(entryPoints);
        //    LOGGER.info("...entry points setted");


    }


}






















