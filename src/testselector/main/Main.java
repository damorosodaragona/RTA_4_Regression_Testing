package testselector.main;


import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;
import testselector.option.OptionParser;
import testselector.project.Project;
import testselector.testSelector.TestSelector;

import java.nio.file.NotDirectoryException;


public class Main {

    private static Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();

        OptionParser optionParser = new OptionParser(args);
        try {
            optionParser.parse();
            Project p = new Project(optionParser.getOldProjectVersionClassPath(), optionParser.getOldProjectVersionTarget());
            Project p1 = new Project(optionParser.getNewProjectVersionClassPath(), optionParser.getNewProjectVersionTarget());

            if (optionParser.getOldProjectVersionOutDir() != null)
                p.saveCallGraph(optionParser.getOldProjectVersionOutDir(), "old");
            if (optionParser.getNewProjectVersionOutDir() != null)
                p1.saveCallGraph(optionParser.getNewProjectVersionOutDir(), "new");

            TestSelector t = new TestSelector(p, p1, optionParser.isAlsoNew());
            t.selectTest().forEach(test -> test.runTest());

        } catch (NoNameException | NoTestFoundedException | NoPathException | NotDirectoryException | ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }




}
