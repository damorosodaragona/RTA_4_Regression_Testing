package testselector.testSelector;

import org.apache.log4j.Logger;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import testselector.main.Main;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

public class Test {

    private static final Logger LOGGER = Logger.getLogger(Main.class);
    private Method testMethod;
    private HashSet<String> testingMethods;

    /**
     * Construct s runnable Test object. Contains information about a test method and which methods this test tests.
     *
     * @param testMethod    the test method
     * @param testingMethod the methods that this test tests.
     */
    public Test(Method testMethod, Set<String> testingMethod) {
        this.testMethod = testMethod;
        this.testingMethods = new HashSet<String>(testingMethod);
    }

    /**
     * Construct s runnable Test object. Contains information about a test method.
     * @param testMethod the test method
     */
    public Test(Method testMethod) {
        this(testMethod, new HashSet<>());
    }

    /**
     * Get the test method
     * @return a method that represent the test
     */
    public Method getTestMethod() {
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
     * Run this test. Can be run JUnit 3, JUnit4 and JUnit 5 test.
     * @return the result of the test.
     */
    public TestExecutionSummary runTest() {

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectMethod(testMethod.getDeclaringClass(), testMethod.getName())
                )
                .build();

        Launcher launcher = LauncherFactory.create();

        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();

        List<TestExecutionSummary.Failure> failures = summary.getFailures();
        if (!failures.isEmpty())
            failures.forEach(failure -> LOGGER.error("The following test case is failed: " + testMethod.getDeclaringClass() + "." + testMethod.getName() + System.lineSeparator() + "caused by: ", failure.getException()));

        if (summary.getTestsSucceededCount() > 0)
            LOGGER.info("The following test case is passed: " + testMethod.getDeclaringClass() + "." + testMethod.getName());

        return summary;

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
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTestMethod());
    }

    public void removeTestingMethod(Method testingToRemove) {
        testingMethods.remove(testingToRemove);
    }
}
