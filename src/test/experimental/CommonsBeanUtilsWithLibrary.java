import CATTO.test.runner.Runner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Before;
import org.junit.Test;
import CATTO.exception.InvalidTargetPaths;
import CATTO.exception.NoTestFoundedException;
import CATTO.project.NewProject;
import CATTO.project.PreviousProject;
import CATTO.project.Project;
import CATTO.reportfromtesting.XMLReport;
import CATTO.test.selector.TestSelector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.util.*;

public class CommonsBeanUtilsWithLibrary extends ExperimentalObjects {


    @Before
    @Override
    public void setUp() {

        this.filter = file -> (Files.isDirectory(file) && !file.toString().endsWith("commons-beanutils-1.9") && !file.toString().endsWith(".metadata") && !file.toString().endsWith(".recommenders") && !file.toString().endsWith("commons-beanutils-1.8")  && !file.toString().endsWith("RemoteSystemsTempFiles"));
        this.path = "/Users/ncdaam/Library/CloudStorage/OneDrive-TUNI.fi/workspace-experimental-object-commons-beanutils".replace("/", File.separator);
        this.target = new String[]{"/Users/ncdaam/Library/CloudStorage/OneDrive-TUNI.fi/workspace-experimental-object-commons-beanutils/commons-beanutils-1.9/bin".replace("/", File.separator)};
        this.libs = new ArrayList<>();

        String lib = "/Users/ncdaam/Library/CloudStorage/OneDrive-TUNI.fi/workspace-experimental-object-commons-beanutils/commons-beanutils-1.9/lib".replace("/", File.separator);


        //get a list of file
        List<File> file = (List<File>) FileUtils.listFiles(new File(lib), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File f : file) {
            libs.add(f.getAbsolutePath());
        }


        //libs.add("C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar");
        libs.add("/Users/ncdaam/Library/CloudStorage/OneDrive-TUNI.fi/versioni_java_precedenti/jre6/lib/rt.jar".replace("/", File.separator));
        libs.add("/Users/ncdaam/Library/CloudStorage/OneDrive-TUNI.fi/versioni_java_precedenti/jre6/lib/jce.jar".replace("/", File.separator));
        //libs.add("C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar");

    }

    @Test
    @Override
    public void lunch(){
        final String[] cls = libs.toArray(new String[0]);

        TreeSet<String> directoryList = getList();

        Project p = null;
        try {
            LOGGER.debug("Creating call-graph of " + target[0]);
            p = new PreviousProject(cls, target);
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

                Project p1 = new NewProject(cls, paths + "/bin".replace("/", File.separator));

                TestSelector rts = new TestSelector(p1, , , , differentObject);

                long start = new Date().getTime();
                LOGGER.info("start in: " + start);

                Set<CATTO.test.Test> selectedTest = rts.selectTest();;

                long end = new Date().getTime();

                LOGGER.info("end in: " + end);

                long time = end - start;

                LOGGER.info("time elapsed: " + time);


                ArrayList<String> selected = new ArrayList<>();
                selectedTest.forEach(test -> {
                    if (test != null) {
                        selected.add(test.getTestMethod().getDeclaringClass().toString() + "#" + test.getTestMethod().getName());
                        try {
                            Runner.run(test, libs.toArray(new String[0]), Arrays.asList(target));
                        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                                 IOException e) {
                            throw new RuntimeException(e);
                        }
                       /*if(id == 22 && test.getTestMethod().getDeclaringClass().getName().equals("org.apache.commons.beanutils.BeanUtils2TestCase") && test.getTestMethod().getName().equals("testSetMappedMap"))
                        test.runTest(); */
                    }
                    else
                        System.out.println("error");
                });

                XMLReport xml = new XMLReport(id, end - start, selected, "RTA-commons-beanutils");
                xml.writeOut();

            } catch (NoTestFoundedException | IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (CATTO.exception.InvalidTargetPaths invalidTargetPaths) {
                invalidTargetPaths.printStackTrace();
            }
            LOGGER.info("Finish Analyzing ProjectTest: " + paths);
            System.gc();
        });









    }
}

