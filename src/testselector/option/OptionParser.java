package testselector.option;

import org.apache.commons.cli.*;
import testselector.exception.NoPathException;

public class OptionParser {


    private final static String OLD_PROJECT_VERSION_LONG_CLASSPATH_OPTION = "old_classpaths";
    private final static String OLD_PROJECT_SHORT_CLASSPATH_OPTION = "old_clsp";
    private final static String NEW_PROJECT_VERSION_LONG_CLASSPATH_OPTION = "new_classpaths";
    private final static String NEW_PROJECT_SHORT_CLASSPATH_OPTION = "new_clsp";
    private final static String OLD_PROJECT_LONG_OUT_DIR_OPTION = "old_outdir";
    private final static String OLD_PROJECT_SHORT_OUT_DIR_OPTION = "old_out";
    private final static String NEW_PROJECT_LONG_OUT_DIR_OPTION = "new_outdir";
    private final static String NEW_PROJECT_SHORT_OUT_DIR_OPTION = "new_out";
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
     *             If an entry point is equal to <ANY> then any externally-callable method is considered as an entry point.
     *             If this argument is omitted, DCF will automatically look for main methods as entry points.
     *             -outfile It is a mandatory argument and represents the XML file where you want to save the analysis results.
     */
    public OptionParser(String[] args) {
        this.args = args;

        options = new Options();
        options.addOption(OLD_PROJECT_SHORT_CLASSPATH_OPTION, OLD_PROJECT_VERSION_LONG_CLASSPATH_OPTION, true, "The oldProjectVersionclasspath you want to use for old version project finding classes.");
        options.addOption(NEW_PROJECT_SHORT_CLASSPATH_OPTION, NEW_PROJECT_VERSION_LONG_CLASSPATH_OPTION, true, "The oldProjectVersionclasspath you want to use for new version project finding classes.");


        Option oldDirOption = new Option(OLD_PROJECT_SHORT_OUT_DIR_OPTION, OLD_PROJECT_LONG_OUT_DIR_OPTION, true, "The folder where you want to save the call-graph generated for the old version project.");
        oldDirOption.setOptionalArg(true);
        Option newDirOption = new Option(NEW_PROJECT_SHORT_OUT_DIR_OPTION, NEW_PROJECT_LONG_OUT_DIR_OPTION, true, "The folder where you want to save the call-graph generated for the new version project.");
        newDirOption.setOptionalArg(true);

        options.addOption(oldDirOption);
        options.addOption(newDirOption);

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

            String path = cmd.getOptionValue(OLD_PROJECT_VERSION_LONG_CLASSPATH_OPTION);
            if (path.isEmpty()) {
                throw new NoPathException();
            }
        classpath = path.split(";");

        return classpath;
    }

    private String[] parseNewProjectVersionClasspathOption(CommandLine cmd) throws NoPathException {
        String[] classpath;

            String path = cmd.getOptionValue(NEW_PROJECT_VERSION_LONG_CLASSPATH_OPTION);
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


