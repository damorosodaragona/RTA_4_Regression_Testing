package CATTO.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static List<String> getFilesPath(String[] path){
        ArrayList<String> paths = new ArrayList<>();
        for(String p : path) {
            List<File> file = (List<File>) FileUtils.listFiles(new File(p), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            for (File f : file) {
                paths.add(f.getAbsolutePath());
            }
        }
    return paths;
    }

}
