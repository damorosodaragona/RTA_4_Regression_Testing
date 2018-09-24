package test.testSelector;


import org.junit.Test;
import testSelector.exception.NoPathException;
import testSelector.exception.NoTestFoundedException;
import testSelector.project.Project;

import java.nio.file.NotDirectoryException;

public class TestPath {
    @Test
    public void loadClass() throws NoTestFoundedException, NotDirectoryException, NoPathException {
        Project p = new Project("D:\\Google Drive Universita\\Università\\Tesi\\p1\\java-testing-example-master\\java-testing-example-master\\example\\target\\classes", "D:\\Google Drive Universita\\Università\\Tesi\\p1\\java-testing-example-master\\java-testing-example-master\\example\\target\\test-classes");
    }
}
