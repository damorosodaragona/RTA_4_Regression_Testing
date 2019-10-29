package testSelector.project;

import soot.Scene;
import testselector.exception.NoTestFoundedException;

import javax.annotation.Nonnull;
import java.nio.file.NotDirectoryException;

public class PreviousProject extends Project {

    public PreviousProject(int junitVersion, String[] classPath, @Nonnull String... target) throws NoTestFoundedException, NotDirectoryException {
        super(junitVersion, classPath, target);

        hierarchy = Scene.v().getActiveHierarchy();
        manageHierarchy();

    }
}
