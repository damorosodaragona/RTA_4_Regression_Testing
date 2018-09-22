package com.test;

import com.company.NoPathExeption;
import com.company.NoTestFoundedException;
import com.company.Project;
import org.junit.Test;

import java.nio.file.NotDirectoryException;

public class TestPath {
    @Test
    public void loadClass() throws NoPathExeption, NotDirectoryException, NoTestFoundedException {
        Project p = new Project("D:\\Google Drive Universita\\Università\\Tesi\\p1\\java-testing-example-master\\java-testing-example-master\\example\\target\\classes", "D:\\Google Drive Universita\\Università\\Tesi\\p1\\java-testing-example-master\\java-testing-example-master\\example\\target\\test-classes");
    }
}
