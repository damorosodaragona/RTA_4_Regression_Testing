import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import testSelector.main.Main;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeSet;


public abstract class ExperimentalObjects {

    static final Logger LOGGER = Logger.getLogger(Main.class);
    protected ArrayList<String> toExclude;

    String path;
    TreeSet<String> directoryList;
    DirectoryStream.Filter<Path> filter;
    String[] target;
    ArrayList<String> libs;

    public ExperimentalObjects() {
        BasicConfigurator.configure();
        setComparator();
    }


    private void setComparator(){
        directoryList = new TreeSet<>((o1, o2) -> {
            String s1 = o1;
            String s = o2;
            if (s1.contains("_") && s.contains("_")) {
                int i1 = Integer.valueOf(s1.split("_")[1]);
                int i2 = Integer.valueOf(s.split("_")[1]);
                if (i1 > i2) {
                    return 1;
                } else if (i1 < i2) {
                    return -1;
                } else
                    return 0;
            }
            return 0;
        });
    }

   public TreeSet<String> getList(){
       Path dir = FileSystems.getDefault().getPath(path);
       try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
           for (Path pat : stream) {
               // Iterate over the paths in the directory and print filenames
               directoryList.add(pat.toString());
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
   return new TreeSet<>(directoryList);
    }

    @Before
    public abstract void setUp();

    @Test
    public abstract void lunch();

}
