package testSelector.main;


import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import testSelector.project.NewProject;
import testSelector.project.PreviousProject;
import testSelector.project.Project;
import testSelector.testSelector.OnlyOneGrapMultiThread;
import testSelector.testSelector.Test;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;
import testselector.option.OptionParser;

import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
public class Main {

    private static Logger LOGGER = Logger.getLogger(Main.class);
    private static Object comparator;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        LOGGER.setLevel(Level.INFO);


        OptionParser optionParser = new OptionParser(args);
            try {
                optionParser.parse();
                Project p1 = new NewProject( 4, optionParser.getNewProjectVersionClassPath(), optionParser.getNewProjectVersionTarget());
                Project p = new PreviousProject( 4, optionParser.getNewProjectVersionClassPath(), optionParser.getNewProjectVersionTarget());

                if (optionParser.getOldProjectVersionOutDir() != null)
                    p.saveCallGraph(optionParser.getOldProjectVersionOutDir(), "old");
                if (optionParser.getNewProjectVersionOutDir() != null)
                    p1.saveCallGraph(optionParser.getNewProjectVersionOutDir(), "new");

                OnlyOneGrapMultiThread t = new OnlyOneGrapMultiThread(p, p1, optionParser.isAlsoNew());

                long start = new Date().getTime();
                LOGGER.info("start in: " + start);

                Set<Test> selectedTest = t.selectTest();
                long end = new Date().getTime();
                LOGGER.info("end in: " + end);
                long time = end - start;
                LOGGER.info("time elapsed: " + time);

                if (optionParser.isRun())
                    selectedTest.forEach(Test::runTest);

                ArrayList<String> selected = new ArrayList<>();
                selectedTest.forEach(test -> {
                    if (test != null)
                        selected.add(test.getTestMethod().getDeclaringClass().toString() + "#" + test.getTestMethod().getName());
                    else
                        System.out.println("error");
                });


            } catch (NoNameException | NoTestFoundedException | NoPathException | NotDirectoryException | ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }

            System.gc();

    }

}
