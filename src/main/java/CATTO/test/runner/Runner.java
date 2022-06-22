package CATTO.test.runner;

import org.apache.log4j.Logger;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import CATTO.test.Test;
import CATTO.util.ClassPathUpdater;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

public class Runner {

    private Runner(){

    }

    static final Logger LOGGER = Logger.getLogger(Runner.class);

    public static TestExecutionSummary run(Test testsToRun, String[] pathForJarFiles, List<String> pathForClassFiles) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {

        //ClassPathUpdater.add(pathForClassFiles);
        //ClassPathUpdater.addJar(pathForJarFiles);


        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectMethod(testsToRun.getTestMethod().getDeclaringClass().toString(),
                                testsToRun.getTestMethod().getName())
                )
                .build();

        Launcher launcher = LauncherFactory.create();

        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();

        List<TestExecutionSummary.Failure> failures = summary.getFailures();
        if (!failures.isEmpty())
            failures.forEach(failure -> LOGGER.error("The following test case is failed: " + testsToRun.getTestMethod().getDeclaringClass() + "." + testsToRun.getTestMethod().getName() + System.lineSeparator() + "caused by: ", failure.getException()));

        if (summary.getTestsSucceededCount() > 0)
            LOGGER.info("The following test case is passed: " + testsToRun.getTestMethod().getDeclaringClass() + "." + testsToRun.getTestMethod().getName());

        return summary;


    }
}


