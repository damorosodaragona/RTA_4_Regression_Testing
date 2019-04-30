package experimental;

import org.junit.runner.JUnitCore;


public class RunAll {
    public static void main(String[] args) {
        JUnitCore.runClasses(SuiteFor.class);
    }
}
