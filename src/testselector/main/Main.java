package testselector.main;


import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;
import testselector.option.OptionParser;
import testselector.project.Project;
import testselector.reportFromTesting.XMLReport;
import testselector.testSelector.IntegralControlFlowTestSelector;
import testselector.testSelector.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;


public class Main {

    private static Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
        ArrayList<String> directoryList = new ArrayList<>();

        //for each modules path

        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path file) throws IOException {
                return (Files.isDirectory(file) && !file.endsWith("commons-beanutils-1.9") && !file.endsWith("commons-beanutils-1.8") && !file.endsWith("closure-compiler-v20160713") &&  !file.endsWith("closure-compiler-v20160619") && !file.toString().contains("commons-codec") && (!file.endsWith(".metadata") && !file.endsWith("commons-codec-1.9") && !file.endsWith("commons-codec-1.8")));
            }
        };

        Path dir = FileSystems.getDefault().getPath("C:\\Users\\Dario\\runtime-EclipseApplication");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
            for (Path path : stream) {
                // Iterate over the paths in the directory and print filenames
               directoryList.add(path.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String [] cls = {"C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar;C:\\Program Files\\Java\\jre6\\lib\\rt.jar;C:\\Program Files\\Java\\jre6\\lib\\jce.jar;C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar;"};
        String [] tgt = {"C:\\Users\\Dario\\runtime-EclipseApplication\\commons-beanutils-1.9\\bin"};
        Project p = null;
        try {
            p = new Project(3, cls, tgt);
        } catch (NoTestFoundedException | NotDirectoryException e) {
            e.printStackTrace();
        }

        Project finalP = p;
        directoryList.forEach(paths -> {
            LOGGER.info("Start Analyzing Project: " + paths);
            args[0] = "-old_target";
            args[1] = "C:\\Users\\Dario\\runtime-EclipseApplication\\closure-compiler-v20160713\\test-classes;C:\\Users\\Dario\\runtime-EclipseApplication\\closure-compiler-v20160713\\build\\classes";
            args[2] = "-new_target";
            args[3] = paths + "\\bin";
            args[4] = "-old_clsp";
            args[5] = "C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\lib\\jce.jar;C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar;";
            int id = Integer.valueOf(paths.split("_")[1]);
                OptionParser optionParser = new OptionParser(args);
            try {
                optionParser.parse();
                Project p1 = new Project(3, optionParser.getNewProjectVersionClassPath(), optionParser.getNewProjectVersionTarget());

                if (optionParser.getOldProjectVersionOutDir() != null)
                    finalP.saveCallGraph(optionParser.getOldProjectVersionOutDir(), "old");
                if (optionParser.getNewProjectVersionOutDir() != null)
                    p1.saveCallGraph(optionParser.getNewProjectVersionOutDir(), "new");

                IntegralControlFlowTestSelector t = new IntegralControlFlowTestSelector(finalP, p1, optionParser.isAlsoNew());

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

                XMLReport xml = new XMLReport(id, end - start, selected);
                xml.writeOut();
            } catch (NoNameException | NoTestFoundedException | NoPathException | NotDirectoryException | ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
            LOGGER.info("Finish Analyzing Project: " + paths);

        });
    }




}
