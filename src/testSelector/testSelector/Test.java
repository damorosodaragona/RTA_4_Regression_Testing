package testSelector.testSelector;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Test {
    private Method testMethod;
    private ArrayList<String> testingMethod;

    public Test(Method testMethod, ArrayList<String> testingMethod) {
        this.testMethod = testMethod;
        this.testingMethod = testingMethod;
    }

    public Test(Method testMethod) {
        this(testMethod, new ArrayList<String>());
    }


}
