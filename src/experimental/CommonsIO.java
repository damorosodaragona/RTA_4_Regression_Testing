package experimental;

import org.junit.Before;
import org.junit.Test;
import testSelector.project.NewProject;
import testSelector.project.PreviousProject;
import testSelector.project.Project;
import testSelector.reportFromTesting.XMLReport;
import testSelector.testSelector.FromTheBottom;
import testselector.exception.NoTestFoundedException;

import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public class CommonsIO extends ExperimentalObjects {


    @Before
    @Override
    public void setUp() {

        this.filter = file -> (Files.isDirectory(file) && !file.toString().endsWith("commons-io-2.4") && !file.toString().endsWith("commons-io-2.5") && !file.toString().endsWith(".metadata")  && !file.toString().endsWith("RemoteSystemsTempFiles"));
        this.path = "C:\\Users\\Dario\\workspace-experimental-object-commons-io";
        this.target = new String[]{"C:\\Users\\Dario\\workspace-experimental-object-commons-io\\commons-io-2.5\\bin"};
        this.libs = new ArrayList<>();


        libs.add("C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar");
        libs.add("C:\\Program Files (x86)\\Java\\jre6\\lib\\rt.jar");
        libs.add("C:\\Program Files (x86)\\Java\\jre6\\lib\\jce.jar");
        libs.add("C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar");


    }

    @Test
    @Override
    public void lunch(){
        final String[] cls = libs.toArray(new String[0]);

        TreeSet<String> directoryList = getList();

        Project p = null;
        try {
            LOGGER.debug("Creating call-graph of " + target[0]);
            p = new PreviousProject( 4, cls, target);
        } catch (NoTestFoundedException | NotDirectoryException e) {
            e.printStackTrace();
        }

        Project finalP = p;
        directoryList.forEach(paths -> {

            try {
                int id = Integer.valueOf(paths.split("_")[1]);

                LOGGER.info("Start Analyzing ProjectTest: " + paths);

                Project p1 = new NewProject( 4, cls, paths + "\\bin");

                FromTheBottom rts = new FromTheBottom(finalP, p1, false);

                long start = new Date().getTime();
                LOGGER.info("start in: " + start);

                Set<testSelector.testSelector.Test> selectedTest = rts.selectTest();;

                long end = new Date().getTime();

                LOGGER.info("end in: " + end);

                long time = end - start;

                LOGGER.info("time elapsed: " + time);


                ArrayList<String> selected = new ArrayList<>();
                selectedTest.forEach(test -> {
                    if (test != null)
                        selected.add(test.getTestMethod().getDeclaringClass().toString() + "#" + test.getTestMethod().getName());
                    else
                        System.out.println("error");
                });

                XMLReport xml = new XMLReport(id, end - start, selected, "RTA-commons-io");
                xml.writeOut();
            } catch (NoTestFoundedException | NotDirectoryException e) {
                LOGGER.error(e.getMessage(), e);
            }
            LOGGER.info("Finish Analyzing ProjectTest: " + paths);
            System.gc();
        });









    }
}

