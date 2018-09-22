package com.company;

import com.sun.istack.internal.logging.Logger;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.lang.reflect.Method;
import java.util.Objects;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

public class Test {
    private static final Logger LOGGER = Logger.getLogger(Test.class);
    private Method methodTest;


    private Failures failures;

    public Test(Method methodTest) {
        setMethodTest(methodTest);
    }

    public Method getMethodTest() {
        return methodTest;
    }

    public void setMethodTest(Method methodTest) {
        this.methodTest = methodTest;
    }


    public Failures run() {

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectMethod(methodTest.getDeclaringClass().getName(), methodTest.getName())
                )
                .build();

        Launcher launcher = LauncherFactory.create();

        failures = new Failures();
        launcher.registerTestExecutionListeners(failures);
        launcher.execute(request);


        return failures;

    }


    public void printFailure() {
        if (!failures.getSummary().getFailures().isEmpty())
            LOGGER.warning("The following test case is failed: " + methodTest.getDeclaringClass() + "." + methodTest.getName() + ":" + System.lineSeparator() + failures.toString());
        // failure ->  LOGGER.warning("The following test case is failed: " + failure.getTestIdentifier() +  "\n" + failure.getException().getMessage() + "\n"));


    }

    public void printSucced() {
        if (failures.getSummary().getTestsSucceededCount() >= 0)
            LOGGER.info("The following test case is passed: " + methodTest.getDeclaringClass() + "." + methodTest.getName());
    }

    public void printResult() {
        if (!failures.getSummary().getFailures().isEmpty())
            LOGGER.warning("The following test case is failed: " + methodTest.getDeclaringClass() + "." + methodTest.getName() + ":" + System.lineSeparator() + failures.toString());
        else if (failures.getSummary().getTestsSucceededCount() >= 0)
            LOGGER.info("The following test case is passed: " + methodTest.getDeclaringClass() + "." + methodTest.getName());
    }

    public TestExecutionSummary getResult() {
        return failures.getSummary();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Test)) return false;
        Test test = (Test) o;
        return Objects.equals(getMethodTest(), test.getMethodTest()); //&&
        //  Objects.equals(failures, test.failures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMethodTest());//, failures);
    }

}
