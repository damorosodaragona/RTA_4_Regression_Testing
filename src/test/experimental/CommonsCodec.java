import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import testselector.exception.InvalidTargetPaths;
import testselector.exception.NoTestFoundedException;
import testselector.project.NewProject;
import testselector.project.PreviousProject;
import testselector.project.Project;
import testselector.reportfromtesting.XMLReport;
import testselector.testselector.FromTheBottom;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public class CommonsCodec extends ExperimentalObjects {


    @Before
    @Override
    public void setUp() {

        this.filter = file -> (Files.isDirectory(file) && !file.toString().endsWith("commons-codec-1.8") && !file.toString().endsWith("commons-codec-1.9") /*&& file.toString().endsWith("commons-codec-1.9_4")*/ && !file.toString().endsWith(".metadata")  && !file.toString().endsWith("RemoteSystemsTempFiles"));
        this.path = "D:\\Users\\Dario\\workspace-experimental-object-commons-codec";
        this.target = new String[]{"D:\\Users\\Dario\\workspace-experimental-object-commons-codec\\commons-codec-1.9\\bin"};
        this.libs = new ArrayList<>();


        libs.add("C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar");
        libs.add("C:\\Program Files\\Java\\jre1.8.0_231\\lib\\rt.jar");
        libs.add("C:\\Program Files\\Java\\jre1.8.0_231\\lib\\jce.jar");
        libs.add("C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar");

    }

    @Test
    @Override
    @Ignore
    public void lunch(){
        final String[] cls = libs.toArray(new String[0]);

        TreeSet<String> directoryList = getList();

        Project p = null;
        try {
            LOGGER.debug("Creating call-graph of " + target[0]);
            p = new PreviousProject( cls, target);
        } catch (NoTestFoundedException | NotDirectoryException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidTargetPaths invalidTargetPaths) {
            invalidTargetPaths.printStackTrace();
        }

        Project finalP = p;
        directoryList.forEach(paths -> {

            try {
                int id = Integer.valueOf(paths.split("_")[1]);

                LOGGER.info("Start Analyzing ProjectTest: " + paths);

                Project p1 = new NewProject( cls, paths + "\\bin");

                FromTheBottom rts = new FromTheBottom(finalP, p1);

                long start = new Date().getTime();
                LOGGER.info("start in: " + start);

                Set<testselector.testselector.Test> selectedTest = rts.selectTest();;

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

                XMLReport xml = new XMLReport(id, end - start, selected, "RTA-commons-codec");
                xml.writeOut();
            } catch (NoTestFoundedException | NotDirectoryException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (InvalidTargetPaths invalidTargetPaths) {
                invalidTargetPaths.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LOGGER.info("Finish Analyzing ProjectTest: " + paths);
            System.gc();
        });









    }
}

