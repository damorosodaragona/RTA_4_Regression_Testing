package testselector.testselector;

import soot.SootMethod;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Test {

    private SootMethod testMethod;
    private HashSet<String> testingMethods;

    /**
     * Construct s runnable Test object. Contains information about a test method and which methods this test tests.
     *
     * @param testMethod    the test method
     * @param testingMethod the methods that this test tests.
     */
    public Test(SootMethod testMethod, Set<String> testingMethod) {
        this.testMethod = testMethod;
        this.testingMethods = new HashSet<>(testingMethod);
    }

    /**
     * Construct s runnable Test object. Contains information about a test method.
     * @param testMethod the test method
     */
    public Test(SootMethod testMethod) {
        this(testMethod, new HashSet<>());
    }

    /**
     * Get the test method
     * @return a method that represent the test
     */
    public SootMethod getTestMethod() {
        return testMethod;
    }

    /**
     * Get all mehtods that this test tests
     * @return a collection of String that represent the name of the merthods that this test tests
     */
    public Set<String> getTestingMethods() {
        return testingMethods;
    }

    /**
     * Add a methot tested by this test.
     * @param testingMethod the name of the method to add
     */
    public void addTestingMethod(String testingMethod) {
        this.testingMethods.add(testingMethod);
    }


    /**
     * Check if two project are equal.
     *
     * @param o the project to confront
     * @return true only if the two Test contain the same test method.
     * only check if the same object is present in the two project.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Test)) return false;
        Test test = (Test) o;
        return Objects.equals(getTestMethod(), test.getTestMethod());
    }

    /**
     * Get the hashcode for this Test calculated  with the method {@link Objects}.hash().
     * @return a int hashcode for this Test.
     **/
    @Override
    public int hashCode() {
        return Objects.hash(getTestMethod());
    }


}
