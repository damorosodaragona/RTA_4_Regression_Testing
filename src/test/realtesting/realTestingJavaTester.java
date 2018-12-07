package realtesting;

import org.junit.Test;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;
import testselector.project.Project;

import java.nio.file.NotDirectoryException;

public class realTestingJavaTester {
    @Test
    public void setUp() throws NoTestFoundedException, NotDirectoryException, NoNameException, NoPathException {
        Project p = new Project("D:\\Google Drive Universita\\Universita\\Tesi\\javaForTestersCode-master\\javaForTestersCode-master\\source\\target\\classes",
                "D:\\Google Drive Universita\\Universita\\Tesi\\javaForTestersCode-master\\javaForTestersCode-master\\source\\target\\generated-sources",
                "D:\\Google Drive Universita\\Universita\\Tesi\\javaForTestersCode-master\\javaForTestersCode-master\\source\\target\\generated-test-sources",
                "D:\\Google Drive Universita\\Universita\\Tesi\\javaForTestersCode-master\\javaForTestersCode-master\\source\\target\\test-classes");
        p.saveCallGraph("D:\\Google Drive Universita\\Universita\\Tesi\\javaForTestersCode-master\\javaForTestersCode-master", "cg");
    }
}
