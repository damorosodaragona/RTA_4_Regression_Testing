package testSelector.project;

import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import testselector.exception.NoTestFoundedException;

import javax.annotation.Nonnull;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;

public class PreviousProject extends Project {
    public PreviousProject(int junitVersion, String[] classPath, @Nonnull String... target) throws NoTestFoundedException, NotDirectoryException {

        this(junitVersion ,classPath, null, target);

    }

    public PreviousProject(int junitVersion, String[] classPath, String[] toExclude, @Nonnull String... target) throws NoTestFoundedException, NotDirectoryException {
        super(junitVersion, classPath, toExclude, target);

        PackManager.v().runPacks();

        hierarchy = Scene.v().getActiveHierarchy();
        Scene.v().releaseActiveHierarchy();
    }

    public void moveToAnotherPackage(ArrayList<SootMethodMoved> moved){
        for(SootMethodMoved methodMoved : moved){
            for(SootClass prevClass : this.getProjectClasses()){
                if(prevClass.getName().equals(methodMoved.getOriginalClass().getName())){
                    for(SootMethod toMove : new ArrayList<>(prevClass.getMethods())){
                        if(toMove.getSubSignature().equals(methodMoved.getMethodMoved().getSubSignature())){
                           for(SootClass inToMove : this.getProjectClasses()){
                               if(inToMove.getName().equals(methodMoved.getMethodMoved().getDeclaringClass().getName())){
                                   SootClass cls = toMove.getDeclaringClass();
                                   cls.removeMethod(toMove);
                                   toMove.setDeclared(false);
                                   toMove.setDeclaringClass(inToMove);
                                   inToMove.addMethod(toMove);
                                   toMove.setDeclared(true);
                               }
                           }
                        }
                    }
                }
            }

        }
    }
}
