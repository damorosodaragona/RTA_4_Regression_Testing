import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Before;
import org.junit.Test;

import testselector.exception.InvalidTargetPaths;
import testselector.exception.NoTestFoundedException;
import testselector.project.NewProject;
import testselector.project.PreviousProject;
import testselector.project.Project;
import testselector.reportfromtesting.XMLReport;
import testselector.testselector.FromTheBottom;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.util.*;

public class CommonsConfiguration extends ExperimentalObjects {


    @Before
    @Override
    public void setUp() {

        this.filter = file -> (Files.isDirectory(file) && !file.toString().endsWith("commons-configuration-1.9") && !file.toString().endsWith("commons-configuration-1.10")  && !file.toString().endsWith(".metadata") && !file.toString().endsWith("RemoteSystemsTempFiles"));
        this.path = "C:\\Users\\Dario\\workspace-experimental-object-commons-configuration";
        this.target = new String[]{"C:\\Users\\Dario\\workspace-experimental-object-commons-configuration\\commons-configuration-1.10\\target\\classes", "C:\\Users\\Dario\\workspace-experimental-object-commons-configuration\\commons-configuration-1.10\\target\\test-classes"};
        this.libs = new ArrayList<>();
        String lib = "C:\\Users\\Dario\\workspace-experimental-object-commons-configuration\\commons-configuration-1.10\\lib";

        //get a list of file
        List<File> file = (List<File>) FileUtils.listFiles(new File(lib), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File f : file) {
            libs.add(f.getAbsolutePath());
        }


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
            p = new PreviousProject(  cls, target);
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
                long start = new Date().getTime();
                LOGGER.info("start in: " + start);
                Project p1 = new NewProject( cls, paths + "\\target\\test-classes", paths + "\\target\\classes");

                FromTheBottom rts = new FromTheBottom(finalP, p1);



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

                XMLReport xml = new XMLReport(id, end - start, selected, "RTA-commons-configuration");
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

