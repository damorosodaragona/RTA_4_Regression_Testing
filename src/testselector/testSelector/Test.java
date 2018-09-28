package testselector.testSelector;

import org.apache.log4j.Logger;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

public class Test {

    private static final Logger LOGGER = Logger.getLogger(Test.class);
    private Method testMethod;
    private HashSet<String> testingMethods;

    public Test(Method testMethod, Set<String> testingMethod) {
        this.testMethod = testMethod;
        this.testingMethods = new HashSet<>(testingMethod);
    }

    public Test(Method testMethod) {
        this(testMethod, new HashSet<>());
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public Set<String> getTestingMethods() {
        return testingMethods;
    }

    public void addTestingMethod(String testingMethod) {
        this.testingMethods.add(testingMethod);
    }

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Test)) return false;
        Test test = (Test) o;
        return Objects.equals(getTestMethod(), test.getTestMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTestMethod());
    }
}
