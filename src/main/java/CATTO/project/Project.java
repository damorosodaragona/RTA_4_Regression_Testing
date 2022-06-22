package CATTO.project;


import org.apache.log4j.Logger;

import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;
import CATTO.exception.InvalidTargetPaths;
import CATTO.exception.NoNameException;
import CATTO.exception.NoPathException;
import CATTO.exception.NoTestFoundedException;
import CATTO.util.JunitUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.*;

public class Project {
    private ArrayList<SootMethod> applicationMethod;
    private HashSet<SootMethod> entryPoints;
    private CallGraph callGraph;
    private ArrayList<String> target;
    private List<SootMethodMoved> moved;
    Hierarchy hierarchy;
    private ArrayList<String> classPath;

    static final Logger LOGGER = Logger.getLogger(Project.class);
    private final HashSet<SootClass> projectClasses;



    public List<String> getClassPath() {
        return new ArrayList<>(classPath);
    }



    public List<SootMethodMoved> getMoved() {
        return new ArrayList<>(moved);
    }

    /**
     * The Project's constructor load in soot all class that are in the paths given as a parametrer,
     * after set all tests method present in project as entry point to produce a CallGraph.
     * @param classPath
     * @param target       the paths of the classes module
     */
    public Project(String[] classPath, String... target) throws IOException, NoTestFoundedException, InvalidTargetPaths {

        //validate the project paths

        validatePaths(target);


        this.classPath = new ArrayList<>();
        this.target = new ArrayList<>();
        this.projectClasses = new HashSet<>();
        this.applicationMethod = new ArrayList<>();
        this.entryPoints = new HashSet<>();
        //this.testingClass = new HashMap<>();
        this.moved = new ArrayList<>();

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

        PackManager.v().runPacks();

        moved = manageHierarchy();

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
        Collections.addAll(this.classPath, classPath);
    }

    /*
     * Check if the paths passed are valid directories or not
     *
     * @param modulePath the project paths
     * @throws NotDirectoryException if the paths passed are not valid directories
     */
    private void validatePaths(String[] modulePath) throws NotDirectoryException, InvalidTargetPaths {
        if(modulePath == null)
            throw new InvalidTargetPaths();
        //are the parameter paths valid?
        for (String s : modulePath) {
            File f;
            try {
                f = new File(s);
            } catch (NullPointerException e) {
                throw new InvalidTargetPaths();

            }


            if (!f.isDirectory())
                throw new NotDirectoryException(f.getAbsolutePath());
        }
    }

    /*
     * Popolate <code>paths</code> ArrayList with the passed string path.
     *
     * @param target
     */
    private void setTarget(String[] target) {
        this.target.addAll(Arrays.asList(target));
    }

    /*
     * Add the application classes loaded in soot in <code>projectClasses</code> ArrayList
     */
    private void setApplicationClass() {
        projectClasses.addAll(Scene.v().getApplicationClasses());
    }


    /**
     * Set the option for soot.
     */
    private void setSootOptions() {
        List<String> argsList = new ArrayList<>();
        argsList.add("-w"); // whole program mode
        argsList.add("-no-bodies-for-excluded"); //don't load bodies for excluded classes, so for non-application-classes
        argsList.add("-allow-phantom-refs"); // allow to don't load some classes (it's necessary for "no-bodies-for-excluded" option)
        argsList.add("-f");
        argsList.add("jimple");
        argsList.add("-p");
        argsList.add("jb.lns");
        argsList.add("sort-locals:true");

        //add all modules path to Soot class-paths
        StringBuilder classPath = new StringBuilder();
        //TODO: capire cosa accade se non si setta il classpath correttamente. Abbiamo degli errori non definiti solo se la versione di junit è la 3.
        //Per junit 4 non abbiamo errori. Probabilmente perchè i jar richiesti sono già nel javaclasspath
        String separator ;
        if (System.getProperty("os.name").toLowerCase().contains("win"))
           separator = ";";
        else
            separator = ":";
/*

        for (int i = 0; i < this.classPath.size(); i++) {

            classPath.append(this.classPath.get(i)).append(separator);
        }
*/
        //Aggiungere un parametro del tipo arraylist di string nel costruttore di Project, NewProject e PreviousProject chiamato toExclude per aggiungere la possibilità di escludere delle classi dall'analisi.
        /*if(toExclude != null) {
            StringBuilder exclude = new StringBuilder();
            for (int i = 0; i < toExclude.length; i++) {
                exclude.append(toExclude[i]).append(";");
            }
            argsList.add("-exclude");
            argsList.add(exclude.toString());

        }*/






        argsList.add("-cp");// Soot class-paths
        //For java >9

        classPath = new StringBuilder("VIRTUAL_FS_FOR_JDK" + File.pathSeparator + this.classPath.get(0));
        argsList.add(classPath.toString());
        //For java <9

        //argsList.add(classPsth.toString());

        //set all modules path as directories to process
        for (String s : target) {
            argsList.add("-process-dir");
            argsList.add(s);
        }

        Options.v().parse(argsList.toArray(new String[0]));


    }



    /*    *//**
     * Save the generated call graph in .dot format. To get a claer callgraph all java,sun,org,jdk,javax methods and calls in the saved callgraph not appear
     *
     * @param path a string that represent the path where save the callgraph
     * @param name the name with wich save the callgraph
     * @throws NoPathException if the path passed is empty or null
     * @throws NoNameException if the name passed is empty or null
     *//*
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
    }*/

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
        return new ArrayList<>(target);
    }

    /**
     * Get the all classes in this project
     *
     * @return a {@link SootClass} List with the path of the modules setted for this project    )
     */
    public Set<SootClass> getProjectClasses() {

        return new HashSet<>(projectClasses);
    }

    /**
     * Get the entry points for this project. The entry points in this case are the tests methods present in this project, so tha {@link CallGraph} start from this entry points.
     *
     * @return a  {@link SootMethod} List which contains the entry points for this project
     */
    public Set<SootMethod> getEntryPoints() {
        return entryPoints;
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
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Project))
            return false;

        Project p = (Project) o;
        Set<SootClass> difference = new HashSet<>(p.getProjectClasses());
        difference.removeAll(this.getProjectClasses());

        return difference.isEmpty();


    }

    public List<SootMethodMoved> manageHierarchy()  {
        HashSet<SootClass> appClass = new HashSet<>(getProjectClasses());
        List<SootMethodMoved> movedToAnotherPackage = new ArrayList<>();

        //for all project classes
        for (SootClass s : appClass) {
            //se è un interfaccia o se è astratta vai avanti
            if (Modifier.isInterface(s.getModifiers()) || Modifier.isAbstract(s.getModifiers()))
                continue;

            SootMethodMoved sootMethodMoved = new SootMethodMoved(s);

            for (SootMethod m : s.getMethods()) {
                //se sono metodi di test aggiungili
                if (JunitUtil.isATestMethod(m))
                    sootMethodMoved.addMethodMoved(m, s);
            }
            //se è un'inner class devo solo prendere i metodi in essa contenuti.
            //le inner class vengono compilate come classi a se che hanno come superclasse la classe nella quale sono definite
            //se gestiamo le gerarchie anche di esse ogni inner class conterrà anche tutti i metodi della classe madre
            //quando eseguiamo i test delle inner class non vengono eseguiti anche i test delle classi madri,
            //diversamente se una classe estende un'altra classe, quando eseguiamo la classe estesa vegnono automaticamente eseguiti
            //anche i test della classe estesa.
            if(!s.isInnerClass()) {
                //fatti dare tutte le superclassi -> in ordine di gerarchia
                List<SootClass> superClasses = Scene.v().getActiveHierarchy().getSuperclassesOf(s);
                //per ogni superclasse
                for (SootClass s1 : superClasses) {
                    //se la classe è una classe di libreria skippa
                    //dammi tutti i metodi della superclasse
                    List<SootMethod> methods = s1.getMethods();
                    //per tutti i metodi della superclasse
                    for (SootMethod m1 : methods) {
                        //se non è un test skippa
                        if (!JunitUtil.isATestMethod(m1))
                            continue;

                        SootMethod toAdd;
                        try {
                            //se la classe figlia ha lo stesso metodo della classe madre
                            toAdd = s.getMethod(m1.getName(), m1.getParameterTypes(), m1.getReturnType());
                            //aggiungi questo metodo tra i metodi della classe s
                            sootMethodMoved.addMethodMoved(toAdd, s);
                        } catch (RuntimeException e) {
                            toAdd = cloneSootMethod(m1);
                            s.addMethod(toAdd);
                            sootMethodMoved.addMethodMoved(toAdd, s1);
                        }


                    }

            }
            if(!sootMethodMoved.getMethodsMoved().isEmpty())
                movedToAnotherPackage.add(sootMethodMoved);

            }
        }


        return movedToAnotherPackage;

    }


    private SootMethod cloneSootMethod(SootMethod sootMethod) {
        SootMethod n = new SootMethod(sootMethod.getName(), sootMethod.getParameterTypes(), sootMethod.getReturnType(), sootMethod.getModifiers());
        Body b = (Body) sootMethod.retrieveActiveBody().clone();


        n.setActiveBody(b);

        //Todo: forse da eliminare
        n.setExceptions(sootMethod.getExceptions());
        n.setPhantom(sootMethod.isPhantom());
        n.setNumber(sootMethod.getNumber());
        n.setSource(sootMethod.getSource());
        return n;
    }



}