package testselector.option;

import org.apache.commons.cli.*;
import testselector.exception.NoPathException;

public class OptionParser {

    private static final String CLASSPATH_ERROR_MESSAGE = "Please indicate the paths dependence (.jar, lib ecc) necessary to run ypu project in this format: pathJar1.jar;pathJar2.jar";
    public static final String TARGET_ERROR_MESSAGE = "Please indicate the paths of modules of the project that you want to analyze in this format: pathModule1;pathModule2";

    private final static String OLD_PROJECT_VERSION_LONG_TARGET_OPTION = "old_target";
    private final static String OLD_PROJECT_SHORT_TARGET_OPTION = "old_tgt";
    private final static String NEW_PROJECT_VERSION_LONG_TARGET_OPTION = "new_target";
    private final static String NEW_PROJECT_SHORT_TARGET_OPTION = "new_tgt";
    private final static String OLD_PROJECT_LONG_OUT_DIR_OPTION = "old_outdir";
    private final static String OLD_PROJECT_SHORT_OUT_DIR_OPTION = "old_out";
    private final static String NEW_PROJECT_LONG_OUT_DIR_OPTION = "new_outdir";
    private final static String NEW_PROJECT_SHORT_OUT_DIR_OPTION = "new_out";
    private final static String ALSO_NEW_METHODS = "as_n";
    private final static String OLD_PROJECT_VERSION_CLASSPATH = "old_clsp";
    private final static String NEW_PROJECT_VERSION_CLASSPATH = "new_clsp";
    private final static String RUN = "run";


    private Options options;
    private String[] args;

    private String[] oldProjectVersionTarget;
    private String[] newProjectVersionTarget;

    private String[] oldProjectVersionClassPath;
    private String[] newProjectVersionClassPath;

    private String oldProjectVersionOutDir;
    private String newProjectVersionOutDir;

    private boolean alsoNew;
    private boolean run;



    /**
     * Constructs a {@link OptionParser} based on the arguments.
     *
     * @param args The arguments, the possible arguments are:<br>
     *             <pre>
     *             <CODE>-old_target</CODE> / <CODE>-old_tgt</CODE> is  mandatory argument. Set the path of the modules of the previous version of the project. The syntax to specify plus modules is: pathModules1,pathModules2<br>
     *             <CODE>-new_target</CODE> / <CODE>-new_tgt</CODE> is  mandatory argument. Set the path of the modules of the new version of the project. The syntax to specify plus modules is: pathModules1,pathModules2<br>
     *             <CODE>-old_clsp</CODE> is  mandatory argument. Set the path of the dependence of the old version of the project (.jar, lib...). The syntax to specify the path of the dependence is: pathDependence1.jar;pathDependence2.jar<br>
     *             <CODE>-new_clsp</CODE> is  optional argument. Set the path of the dependence of the new version of the project (.jar, lib...). The syntax to specify the path of the dependence is: pathDependence1.jar;pathDependence2.jar.
     *                                    If not set ST try to analyze the new version of the project using the dependence set for the previous version of the project.<br>
     *             <CODE>-as_n</CODE> is optional argument. If present ST return and run also the test that testing the new methods implemented in the new version of the project, if its'nt present ST return only the test that testing the different methods finded in the new version of the project.<br>
     *             <CODE>-old_outdir</CODE> / <CODE>-old_out</CODE> is optional argument. This argument if present set the path where save the generated callgraph  for the previous version of the project<br>
     *             <CODE>-new_outdir</CODE> / <CODE>-new_out</CODE> is optional argument. This argument if present set the path where save the generated callgraph  for the NEW version of the project<br>
     *             <CODE>-run</CODE> is optional argument. If present after finding the tests to run, these tests are runned <br>
     *             Example for target class:<br>
     *                   root<br>
     *                   |<br>
     *                   |_____classes_folder<br>
     *                   |<br>
     *                   |_____________package_1<br>
     *                   |<br>
     *                   |_____________package_2<br>
     *<br>
     *                   In this case it's necessary to pass as parameter target the string: "root/classes_folder"<br>
     *
     *                   <br>
     *                   root<br>
     *                   |<br>
     *                   |_____project_classes_folder<br>
     *                   |	        |<br>
     *                   |	        |____________package_1<br>
     *                   |	        |<br>
     *                   |	        |____________package_2<br>
     *                   |<br>
     *                   |<br>
     *                   |____test_project_classes_folder<br>
     *                   |<br>
     *                   |___________test_package_1<br>
     *                   |
     *                   |___________test_package_2<br>
     *<br>
     *                   In this case it's necessary to pass as parameter target the strings: "root/project_classes_folder;root/test_project_classes_folder"
     *</pre>
     */
    public OptionParser(String[] args) {
        this.args = args;

        options = new Options();
        options.addOption(OLD_PROJECT_SHORT_TARGET_OPTION, OLD_PROJECT_VERSION_LONG_TARGET_OPTION, true, "The folder that contains the classes of the old version project to analyze");
        options.addOption(NEW_PROJECT_SHORT_TARGET_OPTION, NEW_PROJECT_VERSION_LONG_TARGET_OPTION, true, "The folder that contains the classes of the new version project to analyze");


        Option oldDirOption = new Option(OLD_PROJECT_SHORT_OUT_DIR_OPTION, OLD_PROJECT_LONG_OUT_DIR_OPTION, true, "The folder where you want to save the call-graph generated for the old version project.");
        oldDirOption.setOptionalArg(true);
        oldDirOption.setRequired(false);

        Option newDirOption = new Option(NEW_PROJECT_SHORT_OUT_DIR_OPTION, NEW_PROJECT_LONG_OUT_DIR_OPTION, true, "The folder where you want to save the call-graph generated for the new version project.");
        newDirOption.setOptionalArg(true);
        newDirOption.setRequired(false);

        Option alsoNewOption = new Option(ALSO_NEW_METHODS, false, "If present return also the test to lunch for the new method, so the methods that aren't in the old project version");
        alsoNewOption.setRequired(false);

        Option oldClsOption = new Option(OLD_PROJECT_VERSION_CLASSPATH, true, "The paths of the dependence to run the old project");
        oldClsOption.setRequired(true);

        Option newClsOption = new Option(NEW_PROJECT_VERSION_CLASSPATH, true, "The paths of the dependence to run the new project. If not present will be set as class path for the new version of the project the same classpath setted for the old project version");
        newClsOption.setRequired(false);

        Option run = new Option(RUN, false, "If present after finding the tests to run, these are runned");
        newClsOption.setRequired(false);


        options.addOption(oldDirOption);
        options.addOption(newDirOption);
        options.addOption(oldClsOption);
        options.addOption(alsoNewOption);
        options.addOption(newClsOption);
        options.addOption(run);
    }

    /**
     * Get the value of the target specified for the new version of the project.
     *
     * @return an asray of String with the module of the new version of the project.
     */
    public String[] getNewProjectVersionTarget() {
        return newProjectVersionTarget;
    }

    /**
     * Get the value of the path where save the genrated callgraph of the new version of the project.
     * @return a String represented the path where save the genrated callgraph of the new version of the project.
     */
    public String getNewProjectVersionOutDir() {
        return newProjectVersionOutDir;
    }

    /**
     * Get the value of the target specified for the old version of the project.
     *
     * @return an asray of String with the module of the old version of the project.
     */
    public String[] getOldProjectVersionTarget() {
        return oldProjectVersionTarget;
    }

    /**
     * Get the value of the path where save the genrated callgraph of the old version of the project.
     * @return a String represented the path where save the genrated callgraph of the old version of the project.
     */
    public String getOldProjectVersionOutDir() {
        return oldProjectVersionOutDir;
    }

    /**
     * Get the value of the classpath specified for the old version of the project.
     *
     * @return an asray of String with the classpath of the old version of the project.
     */
    public String[] getOldProjectVersionClassPath() {
        return oldProjectVersionClassPath;
    }

    /**
     * Get the value of the classpath specified for the new version of the project.
     *
     * @return an asray of String with the classpath of the new version of the project.
     */
    public String[] getNewProjectVersionClassPath() {
        return newProjectVersionClassPath;
    }

    /**
     * Check if the option -as_n is set.
     *
     * @return true if set, false if its'nt set.
     */
    public boolean isAlsoNew() {
        return alsoNew;
    }

    /**
     * Check if the option -run is set.
     *
     * @return true if set, false if its'nt set.
     */
    public boolean isRun() {
        return run;
    }

    /**
     * Parse the options. So takes in input the command line option and set the value for each option present.
     * @throws ParseException if there isn't some mandatory options or if is indicate a not existent option.
     * @throws NoPathException if there isn't value set for the option -old_cls, -new_cls, -old_target, -new_target
     */
    public void parse() throws ParseException, NoPathException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        cmd = parser.parse(options, args);

        oldProjectVersionTarget = parseClassPathTargetOption(cmd, OLD_PROJECT_VERSION_LONG_TARGET_OPTION, TARGET_ERROR_MESSAGE);
        oldProjectVersionClassPath = parseClassPathTargetOption(cmd, OLD_PROJECT_VERSION_CLASSPATH, CLASSPATH_ERROR_MESSAGE);
        newProjectVersionTarget = parseClassPathTargetOption(cmd, NEW_PROJECT_VERSION_LONG_TARGET_OPTION, TARGET_ERROR_MESSAGE);
        newProjectVersionClassPath = parseClassPathTargetOption(cmd, NEW_PROJECT_VERSION_CLASSPATH, CLASSPATH_ERROR_MESSAGE);

        oldProjectVersionOutDir = parseOldProjectVersionOutDirOption(cmd);
        newProjectVersionOutDir = parseNewProjectVersionOutDirOption(cmd);

        alsoNew = parseBinaryOption(cmd, ALSO_NEW_METHODS);
        run = parseBinaryOption(cmd, RUN);

    }

    private boolean parseBinaryOption(CommandLine cmd, String binaryOption) {
        return cmd.hasOption(binaryOption);
    }

    private String[] parseClassPathTargetOption(CommandLine cmd, String optionToParse, String errorMessage) throws NoPathException {
        String[] optionValues;

        String path = cmd.getOptionValue(optionToParse);
        if (path == null || path.isEmpty()) {
            if (!optionToParse.equals(NEW_PROJECT_VERSION_CLASSPATH))
                throw new NoPathException(errorMessage);
            else return parseClassPathTargetOption(cmd, OLD_PROJECT_VERSION_CLASSPATH, CLASSPATH_ERROR_MESSAGE);
        }

        optionValues = path.split(";");

        return optionValues;
    }


    private String parseOldProjectVersionOutDirOption(CommandLine cmd) {
        String outFile = null;
        if (cmd.hasOption(OLD_PROJECT_LONG_OUT_DIR_OPTION)) {
            outFile = cmd.getOptionValue(OLD_PROJECT_LONG_OUT_DIR_OPTION);
        }
        return outFile;
    }

    private String parseNewProjectVersionOutDirOption(CommandLine cmd) {
        String outFile = null;
        if (cmd.hasOption(NEW_PROJECT_LONG_OUT_DIR_OPTION)) {
            outFile = cmd.getOptionValue(NEW_PROJECT_LONG_OUT_DIR_OPTION);
        }
        return outFile;
    }

}


