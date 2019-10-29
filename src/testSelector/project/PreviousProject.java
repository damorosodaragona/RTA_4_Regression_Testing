package testSelector.project;

import soot.Scene;
import testselector.exception.NoTestFoundedException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class PreviousProject extends Project {

    public PreviousProject(int junitVersion, String[] classPath, @Nonnull String... target) throws NoTestFoundedException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        super(junitVersion, classPath, target);

        hierarchy = Scene.v().getActiveHierarchy();

    }
}
