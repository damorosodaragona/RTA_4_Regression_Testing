package CATTO.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Allows programs to modify the classpath during runtime.
 */
public class ClassPathUpdater {
    /** Used to find the method signature. */
    private static final Class[] PARAMETERS = new Class[]{ URL.class };

    /** Class containing the private addURL method. */
    private static final Class<?> CLASS_LOADER = URLClassLoader.class;

    private static final Logger LOGGER = Logger.getLogger(ClassPathUpdater.class);


    private ClassPathUpdater(){

    }

    /**
     * Adds a new paths to the classloader. If the given string points to a file,
     * then that file's parent file (i.e., directory) is used as the
     * directory to add to the classpath. If the given string represents a
     * directory, then the directory is directly added to the classpath.
     *
     * @param paths paths to add at Classpath
     */
    public static void add(List<String> paths)
            throws IOException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        for (String path : paths) {
            add(new File(path + File.separator));
        }
    }

    /**
     * Adds a new path to the classloader. If the given file object is
     * a file, then its parent file (i.e., directory) is used as the directory
     * to add to the classpath. If the given string represents a directory,
     * then the directory it represents is added.
     *
     * @param f The directory (or enclosing directory if a file) to add to the
     * classpath.
     */
    public static void add( File f )
            throws IOException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {



        FileFilter classFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".class");
            }
        };
        File[] files = f.listFiles(classFilter);
        assert files != null;
        for (File file : files){
            try {
                add1(file, f);
            } catch (ClassNotFoundException e) {
                System.out.println("ERROR");
            }
        }



       /* File file;
        file = f.isDirectory() ? f : f.getParentFile();
        try {
            add(file.toURI().toURL());
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR");
        }*/
    }

    /**
     * Adds a new path to the classloader. The class must point to a directory,
     * not a file.
     *
     * @param f The path to include when searching the classpath.
     */
    public static void add1( File f, File directory)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, ClassNotFoundException, MalformedURLException {
        //Method method = CLASS_LOADER.getDeclaredMethod( "addURL", PARAMETERS );
        //method.setAccessible( true );
        //method.invoke( getClassLoader(), url);

        // Creating an instance of URLClassloader using the above URL and parent classloader
        File f1 = new File( "/Users/ncdaam/IdeaProjects/demo1/out/production/demo1");
        ClassLoader loader = URLClassLoader.newInstance(new URL[]{f1.toURI().toURL()}, ClassPathUpdater.class.getClassLoader());
        loader.loadClass(f.getName().replace(".class", ""));





    }

    private static URLClassLoader getClassLoader() {
        return (URLClassLoader)ClassLoader.getSystemClassLoader();
    }

    /**
     * Add dinamycally a list of jar to the library path
     * @param jarFiles one or more string contains the path of the jar to add
     */
    public static void addJar(String... jarFiles ) throws NoSuchMethodException {
        // Get the ClassLoader class
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Class<?> clazz = cl.getClass();

        // Get the protected addURL method from the parent URLClassLoader class
        Method method = null;

        method = clazz.getSuperclass().getDeclaredMethod("addURL", URL.class);

        for(String s : jarFiles){
            File jar = new File(s);
        // Run projected addURL method to add JAR to classpath
        method.setAccessible(true);
            try {
                method.invoke(cl, jar.toURI().toURL());
            } catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
                LOGGER.error(e);
            }
        }
    }

}