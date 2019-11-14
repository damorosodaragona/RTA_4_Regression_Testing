package testselector.project;

import soot.PackManager;
import soot.Scene;
import testselector.exception.InvalidTargetPaths;
import testselector.exception.NoTestFoundedException;

import javax.annotation.Nonnull;
import java.io.IOException;

public class PreviousProject extends Project {

    public PreviousProject(String[] classPath, @Nonnull String... target) throws NoTestFoundedException, IOException, InvalidTargetPaths {
        super(classPath, target);
        PackManager.v().runPacks();
        hierarchy = Scene.v().getActiveHierarchy();
        manageHierarchy();

    }
}
