package testSelector.option;

import org.apache.commons.cli.*;
import testSelector.exception.NoPathException;

public class OptionParser {


    public final static String OLD_PROJECT_VERSION_LONG_CLASSPATH_OPTION = "old_classpaths";
    public final static String OLD_PROJECT_SHORT_CLASSPATH_OPTION = "old_clsp";
    public final static String NEW_PROJECT_VERSION_LONG_CLASSPATH_OPTION = "new_classpaths";
    public final static String NEW_PROJECT_SHORT_CLASSPATH_OPTION = "new_clsp";
    //   public final static String TARGET_DIR = "targetdir";
    public final static String OLD_PROJECT_LONG_OUT_DIR_OPTION = "old_outdir";
    public final static String OLD_PROJECT_SHORT_OUT_DIR_OPTION = "old_out";
    public final static String NEW_PROJECT_LONG_OUT_DIR_OPTION = "new_outdir";
    public final static String NEW_PROJECT_SHORT_OUT_DIR_OPTION = "new_out";
    private Options options;
    private String[] args = null;

    private String[] oldProjectVersionclasspath;


    private String[] newProjectVersionclasspath;

    private String targetDir;
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


        // options.addOption(TARGET_DIR, true,
        //   "The directory containing the classes to be analyzed (typically \"bin\" or \"build\").");
        //  Option entryPointsOption = new Option(ENTRY_POINT, true, "The method from which you want to start the analysis (typically main methods).");
        // entryPointsOption.setOptionalArg(true);
        //ptions.addOption(entryPointsOption);

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
        CommandLine cmd = null;

        cmd = parser.parse(options, args);
        oldProjectVersionclasspath = parseOldProjectVersionClasspathOption(cmd);
        newProjectVersionclasspath = parseNewProjectVersionClasspathOption(cmd);
        oldProjectVersionOutDir = parseOldProjectVersionOutDirOption(cmd);
        newProjectVersionOutDir = parseNewProjectVersionOutDirOption(cmd);
    }

    private String[] parseOldProjectVersionClasspathOption(CommandLine cmd) throws ParseException, NoPathException {
        String[] classpath = null;
        if (cmd.hasOption(OLD_PROJECT_VERSION_LONG_CLASSPATH_OPTION)) {
            String path = cmd.getOptionValue(OLD_PROJECT_VERSION_LONG_CLASSPATH_OPTION);
            if (path == null) {
                throw new NoPathException();
            }
            if (path.isEmpty()) {
                throw new NoPathException();
            }
            String[] parser = path.split(";");
            classpath = parser;
        } else {
            throw new ParseException("The option -" + OLD_PROJECT_VERSION_LONG_CLASSPATH_OPTION + " is missing.");
        }
        return classpath;
    }

    private String[] parseNewProjectVersionClasspathOption(CommandLine cmd) throws ParseException, NoPathException {
        String[] classpath = null;
        if (cmd.hasOption(NEW_PROJECT_VERSION_LONG_CLASSPATH_OPTION)) {
            String path = cmd.getOptionValue(NEW_PROJECT_VERSION_LONG_CLASSPATH_OPTION);
            if (path == null) {
                throw new NoPathException();
            }
            if (path.isEmpty()) {
                throw new NoPathException();
            }
            String[] parser = path.split(";");
            classpath = parser;
        } else {
            throw new ParseException("The option -" + NEW_PROJECT_VERSION_LONG_CLASSPATH_OPTION + " is missing.");
        }
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


