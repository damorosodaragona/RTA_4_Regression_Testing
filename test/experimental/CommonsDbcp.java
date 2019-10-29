package experimental;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Before;
import org.junit.Test;
import testSelector.project.NewProject;
import testSelector.project.PreviousProject;
import testSelector.project.Project;
import testSelector.reportFromTesting.XMLReport;
import testSelector.testSelector.FromTheBottom;
import testselector.exception.NoTestFoundedException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.util.*;

public class CommonsDbcp extends ExperimentalObjects {


    @Before
    @Override
    public void setUp() {

        this.filter = file -> (Files.isDirectory(file) && !file.toString().endsWith("commons-dbcp-2.1") && !file.toString().endsWith("closure-compiler-v20160713_0") && !file.toString().endsWith("commons-dbcp-2.0.1") && !file.toString().endsWith(".metadata") && !file.toString().endsWith("closure-compiler-v20160619") && !file.toString().endsWith("RemoteSystemsTempFiles"));
        this.path = "C:\\Users\\Dario\\workspace-experimental-object-commons-dbcp";
        this.target = new String[]{"C:\\Users\\Dario\\workspace-experimental-object-commons-dbcp\\commons-dbcp-2.1\\target\\classes", "C:\\Users\\Dario\\workspace-experimental-object-commons-dbcp\\commons-dbcp-2.1\\target\\test-classes"};
        this.libs = new ArrayList<>();
        String lib = "C:\\Users\\Dario\\workspace-experimental-object-commons-dbcp\\commons-dbcp-2.1\\lib";

        //get a list of file
        List<File> file = (List<File>) FileUtils.listFiles(new File(lib), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File f : file) {
            libs.add(f.getAbsolutePath());
        }


        libs.add("C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar");
        libs.add("C:\\Program Files\\Java\\jre7\\lib\\rt.jar");
        libs.add("C:\\Program Files\\Java\\jre7\\lib\\jce.jar");
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
            p = new PreviousProject( 3, cls, target);
        } catch (NoTestFoundedException | NotDirectoryException e) {
            e.printStackTrace();
        }

        Project finalP = p;
        directoryList.forEach(paths -> {

            try {
                int id = Integer.valueOf(paths.split("_")[1]);

                LOGGER.info("Start Analyzing ProjectTest: " + paths);

                Project p1 = new NewProject( 3, cls, paths + "\\target\\test-classes", paths + "\\target\\classes");

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

                XMLReport xml = new XMLReport(id, end - start, selected, "RTA-commons-dbcp");
                xml.writeOut();
            } catch (NoTestFoundedException | NotDirectoryException e) {
                LOGGER.error(e.getMessage(), e);
            }
            LOGGER.info("Finish Analyzing ProjectTest: " + paths);
            System.gc();
        });









    }
}

