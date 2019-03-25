package testSelector.project;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import soot.*;
import soot.baf.Baf;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.util.dot.DotGraph;
import soot.util.queue.QueueReader;
import testSelector.util.ClassPathUpdater;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.NotDirectoryException;
import java.util.*;

class SootMethodMoved {




    private SootMethod methodMoved;
    private SootClass originalClass;
    public SootMethodMoved(SootMethod methodMoved, SootClass originalClass){
        this.methodMoved = methodMoved;
        this.originalClass = originalClass;
    }

    public SootMethod getMethodMoved() {
        return methodMoved;
    }

    public SootClass getOriginalClass() {
        return originalClass;
    }
}

public class Project {
    private final String[] toExclude;
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



    Hierarchy hierarchy;
    static final Logger LOGGER = Logger.getLogger(Main.class);


    /**
     * The Project's constructor load in soot all class that are in the paths given as a parametrer,
     * after set all tests method present in project as entry point to produce a CallGraph.
     *
     * @param junitVersion
     * @param classPath
     * @param target       the paths of the classes module
     */
    public Project(int junitVersion, String[] classPath, String[] toExclude, @Nonnull String... target) throws NoTestFoundedException, NotDirectoryException {

        //validate the project paths
        validatePaths(target);

        this.classPath = new ArrayList<>();
        this.target = new ArrayList<>();
        this.projectClasses = new HashSet<>();
        this.applicationMethod = new ArrayList<>();
        this.entryPoints = new HashSet<>();
        this.testingClass = new HashMap<>();
        this.junitVersion = junitVersion;
        this.toExclude = toExclude;
        setTarget(target);

        setClassPath(classPath);

        //reset soot
        soot.G.reset();

        //set soot options
        setSootOptions();
        LOGGER.info("Soot loading necessary classes");

        Scene.v().loadNecessaryClasses();
        setApplicationClass();
        setApplicationMethod();

        try {
            ClassPathUpdater.addJar(this.getClassPath().toArray(new String[0]));
            ClassPathUpdater.add(this.target);
         if(toExclude != null )
            ClassPathUpdater.addJar(toExclude);
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }



    public Hierarchy getHierarchy() {
        return hierarchy;
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
        //argsList.add("-O");
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

        //add all modules path to Soot class-paths
        String classPsth = "";
        for (int i = 0; i < classPath.size(); i++) {
            classPsth += classPath.get(i) + ";";
        }

        if(toExclude != null) {
            StringBuilder exclude = new StringBuilder();
            for (int i = 0; i < toExclude.length; i++) {
                exclude.append(toExclude[i]).append(";");
            }
            argsList.add("-exclude");
            argsList.add(exclude.toString());

        }


        argsList.add("-cp");// Soot class-paths
        argsList.add(classPsth);

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

     void setApplicationMethod() {
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
        if (o == null)
            return false;
        if(!(o instanceof Project))
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






    public void removeEntryPoint(SootMethod entryPoints) {
        this.entryPoints.remove(entryPoints);
    }


}


