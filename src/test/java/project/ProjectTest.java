package project;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import testselector.exception.InvalidTargetPaths;
import testselector.exception.NoTestFoundedException;
import testselector.project.NewProject;
import testselector.project.PreviousProject;
import testselector.project.Project;
import testselector.testselector.FromTheBottom;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.assertTrue;

public class ProjectTest {

    public static final String OUT_PRODUCTION_P ="..\\whatTestProjectForTesting\\out\\production\\p";
    public static final String OUT_PRODUCTION_P_1 = "..\\whatTestProjectForTesting\\out\\production\\p1";
    private final String[] classPath = {"C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\jce.jar;C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar"};
    private final String[] targetWithoutTest = {".\\target\\classes"};


    @Test
    public void getTarget() throws NoSuchMethodException, InvalidTargetPaths, IOException, NoTestFoundedException, IllegalAccessException, InvocationTargetException {
        Project p = null;

        p = new Project( 4, classPath,  OUT_PRODUCTION_P);


        Assert.assertEquals(p.getTarget(),   Arrays.asList(OUT_PRODUCTION_P));
    }

    @Test
    //fintoTest -> vedi todo in Project
    public void testClassPath() throws NoSuchMethodException, InvalidTargetPaths, IOException, NoTestFoundedException, IllegalAccessException, InvocationTargetException {
        String path;
        TreeSet<String> directoryList;
        DirectoryStream.Filter<Path> filter;
        String[] target;
        ArrayList<String> libs;

        Project p = null;
        path = "C:\\Users\\Dario\\workspace-experimental-object-commons-configuration";
        target = new String[]{"C:\\Users\\Dario\\workspace-experimental-object-commons-configuration\\commons-configuration-1.10\\target\\classes", "C:\\Users\\Dario\\workspace-experimental-object-commons-configuration\\commons-configuration-1.10\\target\\test-classes"};
        libs = new ArrayList<>();
        String lib = "C:\\Users\\Dario\\workspace-experimental-object-commons-configuration\\commons-configuration-1.10\\lib";

        //get a list of file
        List<File> file = (List<File>) FileUtils.listFiles(new File(lib), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File f : file) {
            libs.add(f.getAbsolutePath());
        }


        libs.add("C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar");
         libs.add("C:\\Program Files (x86)\\Java\\jre6\\lib\\rt.jar");
        libs.add("C:\\Program Files (x86)\\Java\\jre6\\lib\\jce.jar");
        libs.add("C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar");
        final String[] cls = libs.toArray(new String[0]);

        p = new PreviousProject( 4, cls, target);
       Project  p1 = new NewProject( 4, cls, target);

        FromTheBottom frb = new FromTheBottom(p,p1);
        frb.selectTest();


       // Assert.assertEquals(p.getTarget(),   Arrays.asList(OUT_PRODUCTION_P));
    }
    @Test
    public void getClassPath() throws NoSuchMethodException, InvalidTargetPaths, IOException, NoTestFoundedException, IllegalAccessException, InvocationTargetException {
        Project p = null;



        p = new Project( 4, classPath,  OUT_PRODUCTION_P);


        Assert.assertEquals(p.getClassPath(),   Arrays.asList(classPath));
    }


    @Test(expected = InvalidTargetPaths.class)
    public void noDirectory() throws IOException, NoTestFoundedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {

            new Project(4, null, null, "out/production/p/sootTest/CallGraphExample.class");

    }

    @Test(expected = InvalidTargetPaths.class)
    public void noDirectory2() throws IOException, NoTestFoundedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {

        new Project(4, null, null );

    }

    @Test(expected = NotDirectoryException.class)
    public void noDirectory3() throws IOException, NoTestFoundedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {

        new Project(4, null, "" );

    }

//    @Test(expected = NoNameException.class)
//    public void saveNoNameCallgraph() throws NoPathException, IOException, NoTestFoundedException, NoNameException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {
//        Project p = null;
//
//            p = new Project( 4, classPath, null, OUT_PRODUCTION_P);
//
//        p.saveCallGraph("test", null);
//        p.saveCallGraph("test", "");
//    }

    /*@Test(expected = NoPathException.class)
    public void saveNoPathCallgraph() throws NoPathException, IOException, NoTestFoundedException, NoNameException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {
        Project p = null;

            p = new Project(4,  classPath, null, OUT_PRODUCTION_P);

        p.saveCallGraph(null, "test");
        p.saveCallGraph("", "test");
    }*/

    @Test(expected = NoTestFoundedException.class)
    public void noEntryPoints() throws IOException, NoTestFoundedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {

            new NewProject( 4, classPath,  targetWithoutTest);


    }


    //inserire classPath
    @Test
    public void equalsTest() throws IOException, NoTestFoundedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {
        Project p = null;

            p = new Project( 4, classPath,  OUT_PRODUCTION_P);

        Assert.assertEquals(p, p);

    }

    @Test
    public void noEquals() throws IOException, NoTestFoundedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {
        Project p = null;

            p = new Project(4, classPath,  OUT_PRODUCTION_P);

        Project p1 = null;

            p1 = new Project(4, classPath, OUT_PRODUCTION_P_1);

        Assertions.assertNotEquals(p, p1);

    }

    @Test
    public void differentObjectEquals() throws IOException, NoTestFoundedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {
        Project p = null;

            p = new Project( 4, classPath, OUT_PRODUCTION_P);

        Assertions.assertNotEquals(p, "");

    }

    @Test
    public void nullObjectEquals() throws IOException, NoTestFoundedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {
        Project p = null;

            p = new Project( 4, classPath,  OUT_PRODUCTION_P);

        Assert.assertFalse(p.equals(null));

    }

    @Test
    public void getApplicationMethods() throws IOException, NoTestFoundedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {
        Project p = null;

            p = new Project(4,  classPath, OUT_PRODUCTION_P);

        assertTrue(!p.getApplicationMethod().isEmpty());

    }

    @Test
    public void differentHasCode() throws IOException, NoTestFoundedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {
        Project p = null;

            p = new Project( 4, classPath,  OUT_PRODUCTION_P);

        Project p1 = null;

            p1 = new Project( 4, classPath,  OUT_PRODUCTION_P_1);


        assertTrue(p.hashCode() != p1.hashCode());

    }

    @Test
    public void equalsHasCode() throws IOException, NoTestFoundedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidTargetPaths {
        Project p = null;

            p = new Project(4, classPath, OUT_PRODUCTION_P);

        Project p1 = null;

            p1 = new Project( 4, classPath,  OUT_PRODUCTION_P);


        Assertions.assertNotEquals(p.hashCode(), p1.hashCode());

    }
}
