package project;

import org.junit.Assert;
import org.junit.Test;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;

import java.nio.file.NotDirectoryException;

public class Project {

    @Test(expected = NotDirectoryException.class)
    public void noDirectory() throws NotDirectoryException, NoTestFoundedException {
        new testselector.project.Project("out/production/p/sootTest/CallGraphExample.class");
    }

    @Test(expected = NoNameException.class)
    public void saveNoNameCallgraph() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        p.saveCallGraph(new String("test"), null);
        p.saveCallGraph(new String("test"), new String());
    }

    @Test(expected = NoPathException.class)
    public void saveNoPathCallgraph() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        p.saveCallGraph(null, new String("test"));
        p.saveCallGraph(new String(), new String("test"));
    }

    @Test(expected = NoTestFoundedException.class)
    public void noEntryPoints() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        new testselector.project.Project("com.company");

    }

    @Test
    public void equalsTest() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        Assert.assertTrue(p.equals(p));

    }

    @Test
    public void noEquals() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        testselector.project.Project p1 = new testselector.project.Project("out/production/p1");
        Assert.assertFalse(p.equals(p1));

    }

    @Test
    public void differentObjectEquals() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        Assert.assertFalse(p.equals(new String()));

    }

    @Test
    public void nullObjectEquals() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        Assert.assertFalse(p.equals(null));

    }

    @Test
    public void getApplicationMethods() throws NoPathException, NotDirectoryException, NoTestFoundedException, NoNameException {
        testselector.project.Project p = new testselector.project.Project("out/production/p");
        Assert.assertTrue(!p.getApplicationMethod().isEmpty());

    }
}
