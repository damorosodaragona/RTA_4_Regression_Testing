package testselector;


import org.junit.Test;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;
import testselector.project.Project;

import java.nio.file.NotDirectoryException;

public class TestPath {
    @Test
    public void loadClass() throws NoTestFoundedException, NotDirectoryException, NoPathException, NoNameException {
        new Project(, null, "D:\\Google Drive Universita\\Universita\\Tesi\\p1\\java-testing-example-master\\java-testing-example-master\\example\\target\\classes", "D:\\Google Drive Universita\\Universita\\Tesi\\p1\\java-testing-example-master\\java-testing-example-master\\example\\target\\test-classes").saveCallGraph("C:\\Users\\Dario\\IdeaProjects\\example", "example-ggraph");
        new Project(, null, "C:\\Users\\Dario\\IdeaProjects\\example\\out\\production\\example").saveCallGraph("C:\\Users\\Dario\\IdeaProjects\\example", "evo-example-ggraph");

    }
}
