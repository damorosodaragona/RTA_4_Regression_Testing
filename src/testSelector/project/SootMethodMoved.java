package testSelector.project;

import soot.SootClass;
import soot.SootMethod;

public class SootMethodMoved {



    //todo: non mi serve una classe
    private SootMethod methodMoved;
    private SootClass originalClass;
    public SootMethodMoved(SootMethod methodMoved, SootClass originalClass){
        this.methodMoved = methodMoved;
        this.originalClass = originalClass;

    }

    public SootMethod getMethodMoved() {
        return methodMoved;
    }

    public SootClass getOriginalClass() {
        return originalClass;
    }
}
