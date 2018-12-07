/**
 * Scaffolding file used to store all the setups needed to run
 * tests automatically generated by EvoSuite
 * Tue Oct 09 16:17:18 GMT 2018
 */

package testselector.main;

import org.evosuite.runtime.annotation.EvoSuiteClassExclude;
import org.evosuite.runtime.sandbox.Sandbox;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

@EvoSuiteClassExclude
public class Main_ESTest_scaffolding {

    @org.junit.Rule
    public org.evosuite.runtime.vnet.NonFunctionalRequirementRule nfr = new org.evosuite.runtime.vnet.NonFunctionalRequirementRule();

    private static final java.util.Properties defaultProperties = (java.util.Properties) java.lang.System.getProperties().clone();

    private org.evosuite.runtime.thread.ThreadStopper threadStopper = new org.evosuite.runtime.thread.ThreadStopper(org.evosuite.runtime.thread.KillSwitchHandler.getInstance(), 3000);


    @BeforeClass
    public static void initEvoSuiteFramework() {
        org.evosuite.runtime.RuntimeSettings.className = "testselector.main.Main";
        org.evosuite.runtime.GuiSupport.initialize();
        org.evosuite.runtime.RuntimeSettings.maxNumberOfThreads = 100;
        org.evosuite.runtime.RuntimeSettings.maxNumberOfIterationsPerLoop = 10000;
        org.evosuite.runtime.RuntimeSettings.mockSystemIn = true;
        org.evosuite.runtime.RuntimeSettings.sandboxMode = org.evosuite.runtime.sandbox.Sandbox.SandboxMode.RECOMMENDED;
        org.evosuite.runtime.sandbox.Sandbox.initializeSecurityManagerForSUT();
        org.evosuite.runtime.classhandling.JDKClassResetter.init();
        setSystemProperties();
        initializeClasses();
        org.evosuite.runtime.Runtime.getInstance().resetRuntime();
    }

    @AfterClass
    public static void clearEvoSuiteFramework() {
        Sandbox.resetDefaultSecurityManager();
        java.lang.System.setProperties((java.util.Properties) defaultProperties.clone());
    }

    @Before
    public void initTestCase() {
        threadStopper.storeCurrentThreads();
        threadStopper.startRecordingTime();
        org.evosuite.runtime.jvm.ShutdownHookHandler.getInstance().initHandler();
        org.evosuite.runtime.sandbox.Sandbox.goingToExecuteSUTCode();
        setSystemProperties();
        org.evosuite.runtime.GuiSupport.setHeadless();
        org.evosuite.runtime.Runtime.getInstance().resetRuntime();
        org.evosuite.runtime.agent.InstrumentingAgent.activate();
    }

    @After
    public void doneWithTestCase() {
        threadStopper.killAndJoinClientThreads();
        org.evosuite.runtime.jvm.ShutdownHookHandler.getInstance().safeExecuteAddedHooks();
        org.evosuite.runtime.classhandling.JDKClassResetter.reset();
        resetClasses();
        org.evosuite.runtime.sandbox.Sandbox.doneWithExecutingSUTCode();
        org.evosuite.runtime.agent.InstrumentingAgent.deactivate();
        org.evosuite.runtime.GuiSupport.restoreHeadlessMode();
    }

    public static void setSystemProperties() {

        java.lang.System.setProperties((java.util.Properties) defaultProperties.clone());
        java.lang.System.setProperty("file.encoding", "Cp1252");
        java.lang.System.setProperty("java.awt.headless", "true");
        java.lang.System.setProperty("java.io.tmpdir", "C:\\Users\\Dario\\AppData\\Local\\Temp\\");
        java.lang.System.setProperty("user.country", "IT");
        java.lang.System.setProperty("user.dir", "C:\\Users\\Dario\\IdeaProjects\\soot test context sensitive call graph");
        java.lang.System.setProperty("user.home", "C:\\Users\\Dario");
        java.lang.System.setProperty("user.language", "it");
        java.lang.System.setProperty("user.name", "Dario");
        java.lang.System.setProperty("user.timezone", "Europe/Berlin");
        java.lang.System.setProperty("log4j.configuration", "SUT.log4j.properties");
    }

    private static void initializeClasses() {
        org.evosuite.runtime.classhandling.ClassStateSupport.initializeClasses(Main_ESTest_scaffolding.class.getClassLoader(),
                "org.assertj.core.internal.bytebuddy.dynamic.loading.ByteArrayClassLoader$ChildFirst",
                "testselector.exception.NoPathException",
                "org.assertj.core.internal.bytebuddy.matcher.ElementMatcher",
                "org.assertj.core.internal.bytebuddy.description.type.TypeDefinition",
                "org.apache.log4j.helpers.PatternConverter",
                "org.assertj.core.internal.bytebuddy.dynamic.loading.ByteArrayClassLoader$EmptyEnumeration",
                "org.apache.log4j.DefaultCategoryFactory",
                "org.apache.log4j.AppenderSkeleton",
                "org.apache.log4j.or.RendererMap",
                "testselector.testSelector.Test",
                "org.assertj.core.internal.bytebuddy.description.ModifierReviewable$ForTypeDefinition",
                "org.apache.log4j.ConsoleAppender$SystemErrStream",
                "org.apache.commons.cli.AmbiguousOptionException",
                "net.bytebuddy.dynamic.loading.ByteArrayClassLoader",
                "org.apache.log4j.Level",
                "org.apache.log4j.helpers.DateTimeDateFormat",
                "net.bytebuddy.dynamic.loading.ByteArrayClassLoader$PersistenceHandler$UrlDefinitionAction$ByteArrayUrlStreamHandler",
                "net.bytebuddy.dynamic.loading.ByteArrayClassLoader$EmptyEnumeration",
                "org.apache.log4j.helpers.QuietWriter",
                "org.apache.log4j.ConsoleAppender$SystemOutStream",
                "org.apache.commons.cli.UnrecognizedOptionException",
                "org.apache.log4j.helpers.ISO8601DateFormat",
                "org.assertj.core.internal.bytebuddy.description.ModifierReviewable$OfEnumeration",
                "org.assertj.core.internal.bytebuddy.dynamic.loading.InjectionClassLoader",
                "org.apache.log4j.helpers.PatternParser$LocationPatternConverter",
                "org.apache.log4j.CategoryKey",
                "org.apache.log4j.helpers.PatternParser$BasicPatternConverter",
                "org.apache.log4j.WriterAppender",
                "org.apache.log4j.Layout",
                "org.apache.commons.cli.Util",
                "org.apache.log4j.helpers.OnlyOnceErrorHandler",
                "testselector.main.Main",
                "org.apache.log4j.helpers.Loader",
                "net.bytebuddy.dynamic.loading.ByteArrayClassLoader$PersistenceHandler",
                "org.apache.log4j.ProvisionNode",
                "org.apache.log4j.Hierarchy",
                "org.apache.log4j.helpers.PatternParser$CategoryPatternConverter",
                "testselector.option.OptionParser",
                "org.apache.commons.cli.Option",
                "org.apache.log4j.helpers.PatternParser$LiteralPatternConverter",
                "org.apache.log4j.spi.DefaultRepositorySelector",
                "org.apache.log4j.spi.OptionHandler",
                "org.apache.log4j.spi.RootLogger",
                "org.apache.log4j.spi.ErrorHandler",
                "org.assertj.core.internal.bytebuddy.description.ModifierReviewable$OfAbstraction",
                "org.apache.commons.cli.MissingOptionException",
                "org.apache.log4j.spi.RendererSupport",
                "org.apache.log4j.helpers.AppenderAttachableImpl",
                "org.apache.log4j.helpers.OptionConverter",
                "org.apache.log4j.helpers.FormattingInfo",
                "org.apache.commons.cli.MissingArgumentException",
                "testselector.exception.NoNameException",
                "net.bytebuddy.dynamic.loading.ByteArrayClassLoader$SingletonEnumeration",
                "org.apache.log4j.or.ObjectRenderer",
                "org.apache.log4j.BasicConfigurator",
                "org.apache.log4j.helpers.AbsoluteTimeDateFormat",
                "org.assertj.core.internal.bytebuddy.description.NamedElement",
                "org.assertj.core.internal.bytebuddy.dynamic.loading.ByteArrayClassLoader$SingletonEnumeration",
                "org.apache.commons.cli.DefaultParser",
                "org.apache.log4j.Logger",
                "org.apache.log4j.helpers.PatternParser$ClassNamePatternConverter",
                "org.assertj.core.internal.bytebuddy.description.ModifierReviewable",
                "org.apache.log4j.helpers.PatternParser",
                "org.apache.log4j.helpers.LogLog",
                "org.apache.log4j.Category",
                "org.assertj.core.internal.bytebuddy.dynamic.loading.ByteArrayClassLoader$PersistenceHandler$UrlDefinitionAction",
                "org.apache.log4j.spi.RepositorySelector",
                "org.assertj.core.internal.bytebuddy.dynamic.loading.ByteArrayClassLoader$PersistenceHandler",
                "net.bytebuddy.dynamic.loading.ByteArrayClassLoader$ChildFirst",
                "org.apache.commons.cli.ParseException",
                "org.apache.log4j.spi.LoggerFactory",
                "org.apache.log4j.ConsoleAppender",
                "org.apache.log4j.spi.Configurator",
                "org.assertj.core.internal.bytebuddy.dynamic.loading.ByteArrayClassLoader$PersistenceHandler$UrlDefinitionAction$ByteArrayUrlStreamHandler",
                "org.apache.log4j.or.DefaultRenderer",
                "testselector.exception.NoTestFoundedException",
                "org.assertj.core.internal.bytebuddy.build.HashCodeAndEqualsPlugin$Enhance",
                "org.assertj.core.internal.bytebuddy.build.HashCodeAndEqualsPlugin$Enhance$InvokeSuper",
                "org.apache.commons.cli.Options",
                "org.apache.log4j.spi.ThrowableRendererSupport",
                "org.apache.log4j.PropertyConfigurator",
                "org.assertj.core.internal.bytebuddy.build.HashCodeAndEqualsPlugin$Enhance$InvokeSuper$2",
                "org.assertj.core.internal.bytebuddy.build.HashCodeAndEqualsPlugin$Enhance$InvokeSuper$1",
                "org.apache.log4j.helpers.PatternParser$NamedPatternConverter",
                "org.apache.log4j.Appender",
                "org.apache.commons.cli.OptionValidator",
                "org.apache.commons.cli.CommandLine",
                "org.apache.log4j.spi.HierarchyEventListener",
                "org.apache.commons.cli.CommandLineParser",
                "org.apache.log4j.spi.AppenderAttachable",
                "net.bytebuddy.dynamic.loading.ByteArrayClassLoader$PersistenceHandler$UrlDefinitionAction",
                "org.apache.log4j.helpers.PatternParser$DatePatternConverter",
                "org.apache.log4j.helpers.PatternParser$MDCPatternConverter",
                "org.assertj.core.internal.bytebuddy.build.HashCodeAndEqualsPlugin$Enhance$InvokeSuper$4",
                "org.apache.log4j.Priority",
                "org.assertj.core.internal.bytebuddy.build.HashCodeAndEqualsPlugin$Enhance$InvokeSuper$3",
                "org.assertj.core.internal.bytebuddy.description.ModifierReviewable$OfByteCodeElement",
                "org.apache.log4j.spi.LoggerRepository",
                "org.apache.log4j.PatternLayout",
                "org.apache.log4j.LogManager",
                "org.assertj.core.internal.bytebuddy.dynamic.loading.ByteArrayClassLoader"
        );
    }

    private static void resetClasses() {
        org.evosuite.runtime.classhandling.ClassResetter.getInstance().setClassLoader(Main_ESTest_scaffolding.class.getClassLoader());

        org.evosuite.runtime.classhandling.ClassStateSupport.resetClasses(
                "org.apache.log4j.Category",
                "org.apache.log4j.Logger",
                "org.apache.log4j.Hierarchy",
                "org.apache.log4j.spi.RootLogger",
                "org.apache.log4j.Priority",
                "org.apache.log4j.Level",
                "org.apache.log4j.or.DefaultRenderer",
                "org.apache.log4j.or.RendererMap",
                "org.apache.log4j.DefaultCategoryFactory",
                "org.apache.log4j.spi.DefaultRepositorySelector",
                "org.apache.log4j.helpers.OptionConverter",
                "org.apache.log4j.helpers.Loader",
                "org.apache.log4j.helpers.LogLog",
                "org.apache.log4j.PropertyConfigurator",
                "org.apache.log4j.LogManager",
                "org.apache.log4j.CategoryKey",
                "org.apache.log4j.ProvisionNode",
                "testselector.main.Main",
                "org.apache.log4j.BasicConfigurator",
                "org.apache.log4j.AppenderSkeleton",
                "org.apache.log4j.WriterAppender",
                "org.apache.log4j.ConsoleAppender",
                "org.apache.log4j.Layout",
                "org.apache.log4j.PatternLayout",
                "org.apache.log4j.helpers.PatternParser",
                "org.apache.log4j.helpers.FormattingInfo",
                "org.apache.log4j.helpers.PatternConverter",
                "org.apache.log4j.helpers.PatternParser$BasicPatternConverter",
                "org.apache.log4j.helpers.PatternParser$LiteralPatternConverter",
                "org.apache.log4j.helpers.PatternParser$NamedPatternConverter",
                "org.apache.log4j.helpers.PatternParser$CategoryPatternConverter",
                "org.apache.log4j.helpers.OnlyOnceErrorHandler",
                "org.apache.log4j.helpers.QuietWriter",
                "org.apache.log4j.helpers.AppenderAttachableImpl",
                "testselector.option.OptionParser",
                "org.apache.commons.cli.Options",
                "org.apache.commons.cli.Option",
                "org.apache.commons.cli.OptionValidator",
                "org.apache.commons.cli.DefaultParser",
                "org.apache.commons.cli.CommandLine",
                "org.apache.commons.cli.Util",
                "org.apache.commons.cli.ParseException",
                "org.apache.commons.cli.UnrecognizedOptionException"
        );
    }
}
