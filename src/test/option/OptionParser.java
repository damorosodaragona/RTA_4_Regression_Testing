package option;

import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;
import testselector.exception.NoPathException;

public class OptionParser {

    private static final String OLD_CLSP = "-old_clsp";
    private static final String OLD_CLASS_PATH_VALUE = "ProjectForTesting/p";
    private static final String NEW_CLSP = "-new_clsp";
    private static final String NEW_CLASS_PATH_VALUE = "ProjectForTesting/p1";
    private static final String OLD_DIR_OUT = "-old_out";
    private static final String NEW_DIR_OUT = "-new_out";

    @Test
    public void rightArguments() throws ParseException, NoPathException {
        String[] args1 = {OLD_CLSP,
                OLD_CLASS_PATH_VALUE,
                NEW_CLSP,
                NEW_CLASS_PATH_VALUE,
                OLD_DIR_OUT,
                OLD_CLASS_PATH_VALUE,
                NEW_DIR_OUT,
                NEW_CLASS_PATH_VALUE};

        testselector.option.OptionParser optionParser = new testselector.option.OptionParser(args1);
        optionParser.parse();
        Assert.assertEquals(OLD_CLASS_PATH_VALUE, optionParser.getOldProjectVersionclasspath()[0]);
        Assert.assertEquals(NEW_CLASS_PATH_VALUE, optionParser.getNewProjectVersionclasspath()[0]);
        Assert.assertEquals(OLD_CLASS_PATH_VALUE, optionParser.getOldProjectVersionOutDir());
        Assert.assertEquals(NEW_CLASS_PATH_VALUE, optionParser.getNewProjectVersionOutDir());

    }

    @Test(expected = ParseException.class)
    public void nullOldClspPathArguments() throws ParseException, NoPathException {
        String[] args1 = {OLD_CLSP,
                NEW_CLSP,
                NEW_CLASS_PATH_VALUE,
                OLD_DIR_OUT,
                OLD_CLASS_PATH_VALUE,
                NEW_DIR_OUT,
                NEW_CLASS_PATH_VALUE};

        testselector.option.OptionParser optionParser = new testselector.option.OptionParser(args1);
        optionParser.parse();
    }

    @Test(expected = ParseException.class)
    public void nullNewClspPathArguments() throws ParseException, NoPathException {
        String[] args1 = {OLD_CLSP,
                OLD_CLASS_PATH_VALUE,
                NEW_CLSP,
                OLD_DIR_OUT,
                OLD_CLASS_PATH_VALUE,
                NEW_DIR_OUT,
                NEW_CLASS_PATH_VALUE};

        testselector.option.OptionParser optionParser = new testselector.option.OptionParser(args1);
        optionParser.parse();
    }

    @Test(expected = NoPathException.class)
    public void emptyOldClspPathArguments() throws ParseException, NoPathException {
        String[] args1 = {OLD_CLSP,
                "",
                NEW_CLASS_PATH_VALUE,
                OLD_DIR_OUT,
                OLD_CLASS_PATH_VALUE,
                NEW_DIR_OUT,
                NEW_CLASS_PATH_VALUE};

        testselector.option.OptionParser optionParser = new testselector.option.OptionParser(args1);
        optionParser.parse();
    }

    @Test(expected = NoPathException.class)
    public void emptyNewClspPathArguments() throws ParseException, NoPathException {
        String[] args1 = {OLD_CLSP,
                OLD_CLASS_PATH_VALUE,
                NEW_CLSP,
                "",
                OLD_CLASS_PATH_VALUE,
                NEW_DIR_OUT,
                NEW_CLASS_PATH_VALUE};

        testselector.option.OptionParser optionParser = new testselector.option.OptionParser(args1);
        optionParser.parse();
    }
}
