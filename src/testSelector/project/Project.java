package testSelector.project;

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
import testSelector.exception.NoPathExeption;
import testSelector.exception.NoTestFoundedException;
import testSelector.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.NotDirectoryException;
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
     * @param modulePath the paths of the classes module
     *                   Example:
     *                   root
     *                      |
     *                      |_____classes_folder
     * 	                                |
     * 	                                |_____________package_1
     * 	                                |
     * 	                                |_____________package_2
     * 	                 In this case it's necessary to pass as parameter modulePath the string: "root/classes_folder"
     *
     * 	                 root
     *                      |
     *                      |_____project_classes_folder
     *                      |	        |
     *                      |	        |____________package_1
     *                      |	        |
     *                      |	        |____________package_2
     *                      |
     *                      |
     *                      |____test_project_classes_folder
     *                                  |
     * 	                                |___________test_package_1
     * 	                                |
     * 	                                |___________test_package_2
     *  	             In this case it's necessary to pass as parameter modulePath the strings: "root/project_classes_folder", "root/test_project_classes_folder"
     *                   So, the modulesPath must be the folders which contains the packages folders. So if you have n folders with package you need to pass n string as parameter.
     */
    public Project(@Nonnull String... modulePath) throws NoTestFoundedException, NoPathExeption, NotDirectoryException {
        //     LOGGER.info("Loading project:" + path);

        //validate the project paths
        validatePaths(modulePath);

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


        Scene.v().loadBasicClasses();
        Scene.v().loadDynamicClasses();
        setApplicationClass();

        //set all test methoda in projecy as entry points
        setEntryPoints();
        //run the pack and so the callgraph transformation
        runPacks();


    }

    /**
     * Check if the paths passed are valid directories or not
     *
     * @param modulePath the project paths
     * @throws NoPathExeption        if the paths are null
     * @throws NotDirectoryException if the paths passed are not valid directories
     */
    private void validatePaths(@Nonnull String[] modulePath) throws NoPathExeption, NotDirectoryException {
        //are the parameter paths null?
        if (modulePath.length == 0) {
            throw new NoPathExeption();
        }

        //are the parameter paths valid?
        for (int i = 0; i < modulePath.length; i++) {
            File f = new File(modulePath[i]);
            if (!f.isDirectory())
                throw new NotDirectoryException(f.getAbsolutePath());

        }
    }

    /**
     * Popolate <code>paths</code> ArrayList with the passed string path.
     *
     * @param modulePath
     */
    private void setPaths(@Nonnull String[] modulePath) {
        for (int i = 0; i < modulePath.length; i++) {
            this.paths.add(modulePath[i]);
        }
    }

    /**
     * Add the application classes loaded in soot in <code>projectClasses</code> ArrayList
     */
    private void setApplicationClass() {
        projectClasses.addAll(Scene.v().getApplicationClasses());
    }


    /**
     * Loads the class of the project in soot
     */
    private void loadClassesAndSupport() {
        ArrayList<String> classToLoad = processClasses();
        for (String classPath : classToLoad) {
            //add all classes founded in the passed directory first in SootScene as application class and then in projectClass ArrayList
            addProjectClass(loadClass(classPath));
        }
    }


    /**
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
        argsList.add("-verbose"); //verbose mode
        argsList.add("-W"); // whole program mode
        argsList.add("-no-bodies-for-excluded"); //don't load bodies for excluded classes, so for non-application-classes
        argsList.add("-allow-phantom-refs"); // allow to don't load some classes (it's necessary for "no-bodies-for-excluded" option)
        argsList.add("-cp"); // Soot class-paths
        //add all modules path to Soot class-paths
        for (int i = 0; i < paths.size(); i++) {
            argsList.add(paths.get(i));
        }
        //set all modules path as directories to process
        for (int i = 0; i < paths.size(); i++) {
            argsList.add("-process-dir");
            argsList.add(paths.get(i));
        }
        //parse the option
        Options.v().parse(argsList.toArray(new String[0]));

    }
    //  https://www.spankingtube.com/video/72545/ok-boss-i-m-ready-to-be-strapped-the-extended-cut

    /**
     * Run spark transformation
     */
    private void runPacks() {
        Transform sparkTranform = new Transform("cg.spark", null);

        PhaseOptions.v().setPhaseOption(sparkTranform, "enabled:true"); //enable spark transformation
        PhaseOptions.v().setPhaseOption(sparkTranform, "rta:true"); //enable rta mode for call-graph
        PhaseOptions.v().setPhaseOption(sparkTranform, "verbose:true");
        PhaseOptions.v().setPhaseOption(sparkTranform, "on-fly-cg:false"); //disable default call-graph construction mode (soot not permitted to use rta and on-fly-cg options together)
        PhaseOptions.v().setPhaseOption(sparkTranform, "force-gc:true"); //force call a System.cg() to increase tue available space on garbage collector
        //PhaseOptions.v().setPhaseOption(sparkTranform, "apponly:true"); //indicate to process only the application-classes

        Map<String, String> opt = PhaseOptions.v().getPhaseOptions(sparkTranform); //get the option setted


        LOGGER.info("rta call graph building...");
        sparkTransform(sparkTranform, opt); //build the spark call-graph with the option setted

        //   LOGGER.info("...rta call graph builded");
        CallGraph c = Scene.v().getCallGraph(); //take the call-graph builded
        setCallGraph(c); //set the callgraph as call-graph of this project
        LOGGER.info("Serialize call graph...");

        //save the callgraph
        serializeCallGraph();
        // LOGGER.info("...Serialize call graph completed");

    }

    /**
     * Save the callgraph in the first path passed as parameter
     *
     * @return
     */
    private String getSaveDestination() {
        // String ps[] = paths.get(0).split(File.separator + File.separator);
        //String toElimi = ps[ps.length - 1];
        return paths.get(0);//.replace(toElimi, "");
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


    private void serializeCallGraph() {
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

        String path = getSaveDestination() + File.separator + File.separator + "-call-grsph" + DotGraph.DOT_EXTENSION;
        canvas.plot(path);
        new File(path);
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
        fileToAdd = processDirectory();
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

    /**
     * Add a <code>SootClass</code> in <code>projectClasses</code> ArrayList.
     *
     * @param c the <code>SootClass</code> to add
     */
    public void addProjectClass(SootClass c) {
        projectClasses.add(c);
    }

    /**
     * Check if two project are equal.
     * @param o the project to confront
     * @return true only if the two project contains the same classes; Is not a quality check, two classes can be different
     *          only check if the same object is present in the two project.
     */
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
        for (SootClass sc : p.getProjectClasses()) {
            if (!this.getProjectClasses().contains(sc))
                check = false;
        }
        return check;
    }


    /**
     * Scan all the folders of the project and retunr the class file of the project
     *
     * @return a list that contains all classes of the project in file format
     */
    private List<File> processDirectory() {
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
    private void setEntryPoints() throws NoTestFoundedException {

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
        if (entryPoints.isEmpty())
            throw new NoTestFoundedException();
        this.entryPoints.addAll(entryPoints);
        Scene.v().setEntryPoints(entryPoints);
        //    LOGGER.info("...entry points setted");


    }


}






















