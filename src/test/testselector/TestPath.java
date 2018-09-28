package testselector;


import org.junit.Test;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;
import testselector.project.Project;

import java.nio.file.NotDirectoryException;

public class TestPath {
    @Test
    public void loadClass() throws NoTestFoundedException, NotDirectoryException, NoPathException {
        new Project("D:\\Google Drive Universita\\Università\\Tesi\\p1\\java-testing-example-master\\java-testing-example-master\\example\\target\\classes", "D:\\Google Drive Universita\\Università\\Tesi\\p1\\java-testing-example-master\\java-testing-example-master\\example\\target\\test-classes");
    }
}
