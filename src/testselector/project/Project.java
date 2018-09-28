package testselector.project;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import soot.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//TODO: REfattorizzare: Non si occupa di "troppe cose" questa classe?

public class Project {
    private ArrayList<SootMethod> applicationMethod;
    private ArrayList<SootClass> projectClasses;
    private ArrayList<SootMethod> entryPoints;
    private CallGraph callGraph;
    private ArrayList<String> paths;
    private static final Logger LOGGER = Logger.getLogger(Project.class.getName());
    @Nullable
    private Object o;

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
    public Project(@Nonnull String... modulePath) throws NoTestFoundedException, NotDirectoryException {
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

        //load all class needed
        Scene.v().loadNecessaryClasses();
        Scene.v().loadBasicClasses();
        Scene.v().loadDynamicClasses();

        //add all classes to this project classes
        setApplicationClass();

        //load all methods of this project
        setApplicationMethod();

        //set all test methoda in projecy as entry points
        setEntryPoints();

        //run the pack and so the callgraph transformation
        runPacks();
    }

    /**
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
        List<String> classToLoad = processClasses();
        for (String classPath : classToLoad) {
            //add all classes founded in the passed directory first in SootScene as application class and then in projectClass ArrayList
            loadClass(classPath);
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
        // argsList.add("-no-bodies-for-excluded"); //don't load bodies for excluded classes, so for non-application-classes
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

        CallGraph c = Scene.v().getCallGraph(); //take the call-graph builded
        setCallGraph(c); //set the callgraph as call-graph of this project
        LOGGER.info("Serialize call graph...");
    }

    private void sparkTransform(Transform sparkTranform, Map<String, String> opt) {
            SparkTransformer.v().transform(sparkTranform.getPhaseName(), opt);

    }


    public void saveCallGraph(String path, String name) throws NoPathException, NoNameException {
        if (path == null || path.isEmpty())
            throw new NoPathException();
        if (name == null || name.isEmpty())
            throw new NoNameException();
        DotGraph canvas = new DotGraph(name + "-call-graph");
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


        canvas.plot(path + File.separator + File.separator + name + "-call-graph" + DotGraph.DOT_EXTENSION);
        new File(path);
    }


    public List<SootMethod> getApplicationMethod() {
        return applicationMethod;
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

    public List<String> getPaths() {
        return paths;
    }


    public List<SootClass> getProjectClasses() {
        return projectClasses;
    }

    public List<SootMethod> getEntryPoints() {
        return entryPoints;
    }

    /**
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

    @Override
    public int hashCode() {
        return Objects.hash(getProjectClasses());
    }

    /**
     * Check if two project are equal.
     * @param o the project to confront
     * @return true only if the two project contains the same classes; Is not a quality check, two classes can be different
     *          only check if the same object is present in the two project.
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


    /**
     * Scan all the folders of the project and retunr the class file of the project
     *
     * @return a list that contains all classes of the project in file format
     */
    private List<File> processDirectory() {
        ArrayList<File> classFile = new ArrayList<>();
        //for each modules path
        for (String path : paths) {
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


    /**
     * Set all test-methods of the project as entry point for soot.
     */
    private void setEntryPoints() throws NoTestFoundedException {

        LOGGER.info("setting all test methods as entry points...");
        //get all project classes
        List<SootClass> appClass = getProjectClasses();
        //for all project classes
        for (SootClass s : appClass) {
            //get all methods of class
            List<SootMethod> classMethods = s.getMethods();
            //for each method
            for (SootMethod sootMethod : classMethods) {
                // if is a JUnit test method
                if (Util.isJunitTestCase(sootMethod)) {
                    //add methos as entry point
                    entryPoints.add(sootMethod);

                }
            }


        }
        //if there isn't test
        if (entryPoints.isEmpty())
            //get exception
            throw new NoTestFoundedException();
        //set all test-methods founded as soot entry points
        Scene.v().setEntryPoints(entryPoints);


    }


}






















