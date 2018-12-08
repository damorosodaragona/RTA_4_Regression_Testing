package option;

import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;
import testselector.exception.NoPathException;

public class OptionParser {

    private static final String OLD_TARGET = "-old_target";
    private static final String OLD_TARGET_VALUE = "ProjectForTesting/p";
    private static final String NEW_TARGET = "-new_target";
    private static final String NEW_TARGET_VALUE = "ProjectForTesting/p1";
    private static final String OLD_DIR_OUT = "-old_out";
    private static final String NEW_DIR_OUT = "-new_out";
    private static final String CLASS_PATH = "-old_clsp";
    private static final String CLASS_PATH_VALUE = "C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\jce.jar;C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar;";

    @Test
    public void rightArguments() throws ParseException, NoPathException {
        String[] args1 = {OLD_TARGET,
                OLD_TARGET_VALUE,
                NEW_TARGET,
                NEW_TARGET_VALUE,
                OLD_DIR_OUT,
                OLD_TARGET_VALUE,
                NEW_DIR_OUT,
                NEW_TARGET_VALUE,
                CLASS_PATH,
                CLASS_PATH_VALUE};


        testselector.option.OptionParser optionParser = new testselector.option.OptionParser(args1);
        optionParser.parse();
        Assert.assertEquals(OLD_TARGET_VALUE, optionParser.getOldProjectVersionTarget()[0]);
        Assert.assertEquals(NEW_TARGET_VALUE, optionParser.getNewProjectVersionTarget()[0]);
        Assert.assertEquals(OLD_TARGET_VALUE, optionParser.getOldProjectVersionOutDir());
        Assert.assertEquals(NEW_TARGET_VALUE, optionParser.getNewProjectVersionOutDir());
        String s = new String();
        for (int i = 0; i < optionParser.getOldProjectVersionClassPath().length; i++) {
            s += optionParser.getOldProjectVersionClassPath()[i] + ";";
        }

        Assert.assertEquals(CLASS_PATH_VALUE, s);
        Assert.assertEquals(CLASS_PATH_VALUE, s);


    }

    @Test(expected = ParseException.class)
    public void nullOldClspPathArguments() throws ParseException, NoPathException {
        String[] args1 = {OLD_TARGET,
                NEW_TARGET,
                NEW_TARGET_VALUE,
                OLD_DIR_OUT,
                OLD_TARGET_VALUE,
                NEW_DIR_OUT,
                NEW_TARGET_VALUE};

        testselector.option.OptionParser optionParser = new testselector.option.OptionParser(args1);
        optionParser.parse();
    }

    @Test(expected = ParseException.class)
    public void nullNewClspPathArguments() throws ParseException, NoPathException {
        String[] args1 = {OLD_TARGET,
                OLD_TARGET_VALUE,
                NEW_TARGET,
                OLD_DIR_OUT,
                OLD_TARGET_VALUE,
                NEW_DIR_OUT,
                NEW_TARGET_VALUE};

        testselector.option.OptionParser optionParser = new testselector.option.OptionParser(args1);
        optionParser.parse();
    }

    @Test(expected = NoPathException.class)
    public void emptyOldTargetPathArguments() throws ParseException, NoPathException {
        String[] args1 = {OLD_TARGET,
                "",
                NEW_TARGET,
                NEW_TARGET_VALUE,
                OLD_DIR_OUT,
                OLD_TARGET_VALUE,
                NEW_DIR_OUT,
                NEW_TARGET_VALUE,
                CLASS_PATH,
                CLASS_PATH_VALUE};

        testselector.option.OptionParser optionParser = new testselector.option.OptionParser(args1);
        optionParser.parse();
    }

    @Test(expected = NoPathException.class)
    public void emptyNewTargetPathArguments() throws ParseException, NoPathException {
        String[] args1 = {OLD_TARGET,
                OLD_TARGET_VALUE,
                NEW_TARGET,
                "",
                OLD_DIR_OUT,
                OLD_TARGET_VALUE,
                NEW_DIR_OUT,
                NEW_TARGET_VALUE,
                CLASS_PATH,
                CLASS_PATH_VALUE};

        testselector.option.OptionParser optionParser = new testselector.option.OptionParser(args1);
        optionParser.parse();
    }
}
