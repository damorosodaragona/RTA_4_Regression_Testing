/*
package project;

import org.junit.Assert;
import org.junit.Test;
import testSelector.project.NewProject;
import testSelector.project.Project;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;

import java.nio.file.NotDirectoryException;

import static org.junit.Assert.assertTrue;

public class ProjectTest {

    public static final String OUT_PRODUCTION_P ="out/production/p";
    public static final String OUT_PRODUCTION_P_1 = "out/production/p1";
    private final String[] classPath = {"C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\jce.jar;C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar"};

    @Test(expected = NotDirectoryException.class)
    public void noDirectory() throws NotDirectoryException, NoTestFoundedException {
        new Project(4, null, null, "out/production/p/sootTest/CallGraphExample.class");
    }

    @Test(expected = NoNameException.class)
    public void saveNoNameCallgraph() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        Project p = new Project( 4, classPath, null, OUT_PRODUCTION_P);
        p.saveCallGraph("test", null);
        p.saveCallGraph("test", "");
    }

    @Test(expected = NoPathException.class)
    public void saveNoPathCallgraph() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        Project p = new Project (4,  classPath, null, OUT_PRODUCTION_P);
        p.saveCallGraph(null, "test");
        p.saveCallGraph("", "test");
    }

    @Test(expected = NoTestFoundedException.class)
    public void noEntryPoints() throws NotDirectoryException, NoTestFoundedException {
        new NewProject( 4, classPath, (String[]) null, "com.company").createCallgraph();

    }


    //inserire classPath
    @Test
    public void equalsTest() throws NotDirectoryException, NoTestFoundedException {
        Project p = new Project( 4, classPath, null, OUT_PRODUCTION_P);
        Assert.assertEquals(p, p);

    }

    @Test
    public void noEquals() throws NotDirectoryException, NoTestFoundedException {
        Project p = new Project(4, classPath, null, OUT_PRODUCTION_P);
        Project p1 = new Project(4, classPath, null, OUT_PRODUCTION_P_1);
        Assert.assertNotEquals(p, p1);

    }

    @Test
    public void differentObjectEquals() throws NotDirectoryException, NoTestFoundedException {
        Project p = new Project( 4, classPath, null,OUT_PRODUCTION_P);
        Assert.assertNotEquals(p, "");

    }

    @Test
    public void nullObjectEquals() throws NotDirectoryException, NoTestFoundedException {
        Project p = new Project( 4, classPath, null, OUT_PRODUCTION_P);
        Assert.assertFalse(p.equals(null));

    }

    @Test
    public void getApplicationMethods() throws NotDirectoryException, NoTestFoundedException {
        Project p = new Project(4,  classPath, null, OUT_PRODUCTION_P);
        assertTrue(!p.getApplicationMethod().isEmpty());

    }

    @Test
    public void differentHasCode() throws NotDirectoryException, NoTestFoundedException {
        Project p = new Project( 4, classPath, null, OUT_PRODUCTION_P);
        Project p1 = new Project( 4, classPath, null, OUT_PRODUCTION_P_1);

        assertTrue(p.hashCode() != p1.hashCode());

    }

    @Test
    public void equalsHasCode() throws NotDirectoryException, NoTestFoundedException {
        Project p = new Project(4, classPath, null, OUT_PRODUCTION_P);
        Project p1 = new Project( 4, classPath, null, OUT_PRODUCTION_P);

        Assert.assertNotEquals(p.hashCode(), p1.hashCode());

    }
}
*/
