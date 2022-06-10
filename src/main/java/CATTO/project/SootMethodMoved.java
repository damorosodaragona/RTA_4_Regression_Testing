package CATTO.project;

import soot.SootClass;
import soot.SootMethod;

import java.util.HashSet;
import java.util.Set;

public class SootMethodMoved {



    private HashSet<SootMethod> methodsMoved;
    private SootClass inToMoved;
    private HashSet<SootClass> originalsSootClass;

    public SootMethodMoved(SootMethod methodMoved, SootClass inToMoved){
        this.methodsMoved = new HashSet<>();
        this.originalsSootClass = new HashSet<>();
        this.methodsMoved.add(methodMoved);
        this.inToMoved = inToMoved;

    }

    public SootMethodMoved(SootClass inToMoved){
        this.methodsMoved = new HashSet<>();
        this.originalsSootClass = new HashSet<>();


        this.inToMoved = inToMoved;

    }

    public void addMethodMoved(SootMethod methodMoved, SootClass originalSootClass){
        this.methodsMoved.add(methodMoved);
        if(!methodMoved.getDeclaringClass().equals(originalSootClass)) {
            originalsSootClass.add(originalSootClass);
        }
    }

    public Set<SootMethod> getMethodsMoved() {
        return new HashSet<>(methodsMoved);
    }


    public boolean isMoved(SootMethod toCheck){
        boolean isMoved = false;

            if(originalsSootClass.contains(toCheck.getDeclaringClass())){
                for(SootMethod m : methodsMoved){
                    if(m.getName().equals(toCheck.getName()) && m.getSubSignature().equals(toCheck.getSubSignature()) ) {
                        isMoved = true;
                        break;
                    }
                }

            }



        return isMoved;

    }

    public SootClass getInToMoved() { return inToMoved; }

    public HashSet<SootClass> getOriginalClasses() {
        return new HashSet<>(originalsSootClass);
    }
}
