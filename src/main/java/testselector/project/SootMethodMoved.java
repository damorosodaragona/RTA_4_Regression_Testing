package testselector.project;

import soot.SootClass;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.HashSet;

public class SootMethodMoved {



    private HashSet<SootMethod> methodsMoved;
    private SootClass inToMoved;
    private ArrayList<SootClass> originalsSootClass;

    public SootMethodMoved(SootMethod methodMoved, SootClass inToMoved){
        this.methodsMoved = new HashSet<>();
        this.originalsSootClass = new ArrayList<>();
        this.methodsMoved.add(methodMoved);
        this.inToMoved = inToMoved;

    }

    public SootMethodMoved(SootClass inToMoved){
        this.methodsMoved = new HashSet<>();
        this.originalsSootClass = new ArrayList<>();


        this.inToMoved = inToMoved;

    }

    public void addMethodMoved(SootMethod methodMoved, SootClass originalSootClass){
        this.methodsMoved.add(methodMoved);
        if(!methodMoved.getDeclaringClass().equals(originalSootClass)) {
            originalsSootClass.add(originalSootClass);
        }
    }

    public HashSet<SootMethod> getMethodsMoved() {
        return new HashSet<SootMethod>(methodsMoved);
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
}
