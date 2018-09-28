package project;

import org.junit.Assert;
import org.junit.Test;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;

import java.nio.file.NotDirectoryException;

import static org.junit.Assert.assertTrue;

public class Project {

    @Test(expected = NotDirectoryException.class)
    public void noDirectory() throws NotDirectoryException, NoTestFoundedException {
        new testselector.project.Project("out/production/p/sootTest/CallGraphExample.class");
    }

    @Test(expected = NoNameException.class)
    public void saveNoNameCallgraph() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        p.saveCallGraph("test", null);
        p.saveCallGraph("test", "");
    }

    @Test(expected = NoPathException.class)
    public void saveNoPathCallgraph() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        p.saveCallGraph(null, "test");
        p.saveCallGraph("", "test");
    }

    @Test(expected = NoTestFoundedException.class)
    public void noEntryPoints() throws NotDirectoryException, NoTestFoundedException {
        new testselector.project.Project("com.company");

    }

    @Test
    public void equalsTest() throws NotDirectoryException, NoTestFoundedException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        Assert.assertEquals(p, p);

    }

    @Test
    public void noEquals() throws NotDirectoryException, NoTestFoundedException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        testselector.project.Project p1 = new testselector.project.Project("out/production/p1");
        Assert.assertNotEquals(p, p1);

    }

    @Test
    public void differentObjectEquals() throws NotDirectoryException, NoTestFoundedException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        Assert.assertNotEquals(p, "");

    }

    @Test
    public void nullObjectEquals() throws NotDirectoryException, NoTestFoundedException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        Assert.assertFalse(p.equals(null));

    }

    @Test
    public void getApplicationMethods() throws NotDirectoryException, NoTestFoundedException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        assertTrue(!p.getApplicationMethod().isEmpty());

    }

    @Test
    public void differentHasCode() throws NotDirectoryException, NoTestFoundedException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        testselector.project.Project p1 = new testselector.project.Project("out/production/p1");

        assertTrue(p.hashCode() != p1.hashCode());

    }

    @Test
    public void equalsHasCode() throws NotDirectoryException, NoTestFoundedException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        testselector.project.Project p1 = new testselector.project.Project("out/production/p");

        Assert.assertNotEquals(p.hashCode(), p1.hashCode());

    }
}
