package testselector.option;

import org.apache.commons.cli.*;
import testselector.exception.NoPathException;

public class OptionParser {


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


    private Options options;
    private String[] args;

    private String[] oldProjectVersionclasspath;
    private String[] newProjectVersionclasspath;

    private String oldProjectVersionOutDir;
    private String newProjectVersionOutDir;

    /**
     * Constructs a {@link OptionParser} based on the arguments.
     *
     * @param args The arguments, which can be either optional or mandatory. The possible arguments are:
     *             -previousProjectVersionclasspath. It is a mandatory argument and represents the previousProjectVersionclasspath of the program being analyzed. The paths, specified in the previousProjectVersionclasspath, must be colon-separated in Unix-like OS while semicolon-separated in Windows OS (i.e., (like the –cp argument of JVM).
     *             -targetdir. It is a mandatory argument and represents the path to the directory containing the classes being analyzed (typically "bin" or "build" or "target/classes").
     *             -entrypoint. It is an optional argument and represents the method from which to start the analysis. An entry point must be specified in the following way: <code>foo.Foo: void main(java.lang.String[])</code>. This means that the main method of <code>foo.Foo</code> is the entry point. If you want to provide n entry points, you have to specify n –entrypoint arguments.
     */
    //Todo; aggiungere parser opzione classpath. (target -> le classi da analizzare; classpath -> le dipendenze)
    //Todo; aggiungere parsser per opzione also new;

    public OptionParser(String[] args) {
        this.args = args;

        options = new Options();
        options.addOption(OLD_PROJECT_SHORT_TARGET_OPTION, OLD_PROJECT_VERSION_LONG_TARGET_OPTION, true, "The folder that contains the classes of the old version project to analyze");
        options.addOption(NEW_PROJECT_SHORT_TARGET_OPTION, NEW_PROJECT_VERSION_LONG_TARGET_OPTION, true, "The folder that contains the classes of the new version project to analyze");


        Option oldDirOption = new Option(OLD_PROJECT_SHORT_OUT_DIR_OPTION, OLD_PROJECT_LONG_OUT_DIR_OPTION, true, "The folder where you want to save the call-graph generated for the old version project.");
        oldDirOption.setOptionalArg(true);
        oldDirOption.setRequired(true);

        Option newDirOption = new Option(NEW_PROJECT_SHORT_OUT_DIR_OPTION, NEW_PROJECT_LONG_OUT_DIR_OPTION, true, "The folder where you want to save the call-graph generated for the new version project.");
        newDirOption.setOptionalArg(true);
        newDirOption.setRequired(true);

        Option alsoNewOption = new Option(ALSO_NEW_METHODS, false, "If enabled return also the test to lunch for the new method, so the methods that aren't in the old project version");
        alsoNewOption.setRequired(false);

        Option oldClsOption = new Option(OLD_PROJECT_VERSION_CLASSPATH, true, "The paths of the dependence to run the old project");
        oldClsOption.setRequired(true);

        Option newClsOption = new Option(NEW_PROJECT_VERSION_CLASSPATH, true, "The paths of the dependence to run the new project. If not present will be set as class path for the new version of the project the same classpath setted for the old project version");
        newClsOption.setRequired(false);

        options.addOption(oldDirOption);
        options.addOption(newDirOption);
        options.addOption(oldClsOption);
        options.addOption(alsoNewOption);
        options.addOption(newClsOption);
    }

    public String[] getNewProjectVersionclasspath() {
        return newProjectVersionclasspath;
    }

    public String getNewProjectVersionOutDir() {
        return newProjectVersionOutDir;
    }

    public String[] getOldProjectVersionclasspath() {
        return oldProjectVersionclasspath;
    }


    public String getOldProjectVersionOutDir() {
        return oldProjectVersionOutDir;
    }

    public void parse() throws ParseException, NoPathException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        cmd = parser.parse(options, args);
        oldProjectVersionclasspath = parseOldProjectVersionClasspathOption(cmd);
        newProjectVersionclasspath = parseNewProjectVersionClasspathOption(cmd);
        oldProjectVersionOutDir = parseOldProjectVersionOutDirOption(cmd);
        newProjectVersionOutDir = parseNewProjectVersionOutDirOption(cmd);
    }

    private String[] parseOldProjectVersionClasspathOption(CommandLine cmd) throws NoPathException {
        String[] classpath;

        String path = cmd.getOptionValue(OLD_PROJECT_VERSION_LONG_TARGET_OPTION);
            if (path.isEmpty()) {
                throw new NoPathException();
            }
        classpath = path.split(";");

        return classpath;
    }

    private String[] parseNewProjectVersionClasspathOption(CommandLine cmd) throws NoPathException {
        String[] classpath;

        String path = cmd.getOptionValue(NEW_PROJECT_VERSION_LONG_TARGET_OPTION);
            if (path.isEmpty()) {
                throw new NoPathException();
            }
        classpath = path.split(";");

        return classpath;
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


