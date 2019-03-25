package testSelector.main;


import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import testSelector.project.NewProject;
import testSelector.project.PreviousProject;
import testSelector.project.Project;
import testSelector.reportFromTesting.XMLReport;
import testSelector.testSelector.OnlyOneGrapMultiThread;
import testSelector.testSelector.Test;
import testselector.exception.NoNameException;
import testselector.exception.NoPathException;
import testselector.exception.NoTestFoundedException;
import testselector.option.OptionParser;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
public class MainModified {

    private static Logger LOGGER = Logger.getLogger(Main.class);
    private static Object comparator;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        LOGGER.setLevel(Level.INFO);
        TreeSet<String> directoryList = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String s1 =  o1;
                String s =  o2;
                if(s1.contains("_") && s.contains("_")){
                    int i1 = Integer.valueOf(s1.split("_")[1]);
                    int i2 = Integer.valueOf(s.split("_")[1]);
                    if(i1>i2){
                        return 1;
                    }else if(i1<i2){
                        return -1;
                    }else
                        return 0;
                }
                return 0;
            }
        });
        //  PropertyConfigurator.configure("C:\\Users\\Dario\\IdeaProjects\\soot test context sensitive call graph\\log4j.properties");
        //for each modules path
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path file) throws IOException {
                //analizza commons-configuration esclude commons-beanutils e  commnons-codec.
                return (Files.isDirectory(file) && !file.toString().endsWith("commons-math-3.1") && !file.toString().endsWith("commons-math-3.0")  && !file.toString().endsWith("commons-lang-3.2") && !file.toString().endsWith("commons-lang-3.1") && !file.toString().endsWith("commons-jxpath-1.2") && !file.toString().endsWith("commons-jxpath-1.3") && !file.toString().endsWith("RemoteSystemsTempFiles") && !file.toString().endsWith("weka-3.6.10") && !file.toString().endsWith("commons-dbcp-2.1") && !file.toString().endsWith("commons-io-2.5") && !file.toString().endsWith("commons-io-2.4") && !file.toString().endsWith("commons-dbcp-2.0.1") && !file.toString().endsWith("weka-3.6.10_0") && !file.toString().endsWith("weka-3.6.9")&& !file.toString().endsWith(".idea")  && !file.endsWith("commons-configuration-1.10") && !file.endsWith("commons-configuration-1.9") && !file.endsWith("commons-beanutils-1.9") && !file.endsWith("commons-beanutils-1.8") && !file.endsWith("closure-compiler-v20160713") && !file.endsWith("closure-compiler-v20160619") && (!file.endsWith(".metadata") && !file.endsWith("commons-codec-1.9") && !file.endsWith("commons-codec-1.8")));
            }
        };

        //   librerie per commons-configuration
      /*  String lib = "D:\\Users\\Dario\\workspace-experimental-object-commons-configuration\\commons-configuration-1.10\\lib";

        ArrayList<String> libs = new ArrayList<>();
        //get a list of file
        List<File> file = (List<File>) FileUtils.listFiles(new File(lib), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File f : file) {
            libs.add(f.getAbsolutePath());
        }


        libs.add("C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar");
        libs.add("C:\\Program Files (x86)\\Java\\jre6\\lib\\rt.jar");
        libs.add("C:\\Program Files (x86)\\Java\\jre6\\lib\\jce.jar");
        libs.add("C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar");
        final String[] cls = libs.toArray(new String[0]);

     //   path per commons-configuration
        String path = "D:\\Users\\Dario\\workspace-experimental-object-commons-configuration";

     //   target per commons-configuration
           String[] tgt = {"D:\\Users\\Dario\\workspace-experimental-object-commons-configuration\\commons-configuration-1.10\\target\\classes", "D:\\Users\\Dario\\workspace-experimental-object-commons-configuration\\commons-configuration-1.10\\target\\test-classes"};
*/

        //librerie per weka
    /*  String lib = "D:\\Users\\Dario\\workspace-experimental-object-weka\\weka-3.6.10\\lib";

        ArrayList<String> libs = new ArrayList<>();
        //get a list of file
        List<File> file = (List<File>) FileUtils.listFiles(new File(lib), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File f : file) {
            libs.add(f.getAbsolutePath());
        }


        libs.add("C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar");
        libs.add("C:\\Program Files (x86)\\Java\\jre6\\lib\\rt.jar");
        libs.add("C:\\Program Files (x86)\\Java\\jre6\\lib\\jce.jar");
        libs.add("C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar");
        final String[] cls = libs.toArray(new String[0]);

        //   path per weka
        String path = "D:\\Users\\Dario\\workspace-experimental-object-weka";

        //   target per weka
        String[] tgt = {"D:\\Users\\Dario\\workspace-experimental-object-weka\\weka-3.6.10\\build\\classes", "D:\\Users\\Dario\\workspace-experimental-object-weka\\weka-3.6.10\\build\\testcases"};
*/

        //librerie per commons-dbcp
     /*   String lib = "C:\\Users\\Dario\\workspace-experimental-object-commons-dbcp\\commons-dbcp-2.1\\lib";

        ArrayList<String> libs = new ArrayList<>();
        //get a list of file
        List<File> file = (List<File>) FileUtils.listFiles(new File(lib), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File f : file) {
            libs.add(f.getAbsolutePath());
        }


        libs.add("C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar");
        libs.add("C:\\Program Files\\Java\\jre7\\lib\\rt.jar");
        libs.add("C:\\Program Files\\Java\\jre7\\lib\\jce.jar");
        libs.add("C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar");
        final String[] cls = libs.toArray(new String[0]);

        //   path per commons-dbcp
        String path = "C:\\Users\\Dario\\workspace-experimental-object-commons-dbcp";

        //   target per commons-dbcp
        String[] tgt = {"C:\\Users\\Dario\\workspace-experimental-object-commons-dbcp\\commons-dbcp-2.1\\target\\classes", "C:\\Users\\Dario\\workspace-experimental-object-commons-dbcp\\commons-dbcp-2.1\\target\\test-classes"};
*/
       /* //librerie per commons-io
        ArrayList<String> libs = new ArrayList<>();
        libs.add("C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar");
        libs.add("C:\\Program Files (x86)\\Java\\jre6\\lib\\rt.jar");
        libs.add("C:\\Program Files (x86)\\Java\\jre6\\lib\\jce.jar");
        libs.add("C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar");
        final String[] cls = libs.toArray(new String[0]);

        //   path per commons-io
        String path = "C:\\Users\\Dario\\workspace-experimental-object-commons-io";

        //   target per commons-io
        String[] tgt = {"C:\\Users\\Dario\\workspace-experimental-object-commons-io\\commons-io-2.5\\bin"};*/

////
//
//        //path per commons-codec
//        String path = "C:\\Users\\Dario\\workspace-experimental-object-commons-codec";
//
//        //target per commons-codec
//        String[] tgt = {"C:\\Users\\Dario\\workspace-experimental-object-commons-codec\\commons-codec-1.9\\bin"};

        //librerie per commons-jxpath
/*
        String lib = "C:\\Users\\Dario\\workspace-experimental-object-commons-jxpath\\commons-jxpath-1.3\\lib";

        ArrayList<String> libs = new ArrayList<>();
        //get a list of file
        List<File> file = (List<File>) FileUtils.listFiles(new File(lib), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File f : file) {
            libs.add(f.getAbsolutePath());
        }


       */
/* libs.add("C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar");
        libs.add("C:\\Program Files\\Java\\jre1.8.0_201\\lib\\rt.jar");
        libs.add("C:\\Program Files\\Java\\jre1.8.0_201\\lib\\jce.jar");
        libs.add("C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar");*//*

        final String[] cls = libs.toArray(new String[0]);

        //   path per commons-jxpath
        String path = "C:\\Users\\Dario\\workspace-experimental-object-commons-jxpath";

        //   target per commons-jxpath
        String[] tgt = {"C:\\Users\\Dario\\workspace-experimental-object-commons-jxpath\\commons-jxpath-1.3\\bin"};


        Path dir = FileSystems.getDefault().getPath(path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
            for (Path pat : stream) {
                // Iterate over the paths in the directory and print filenames
                directoryList.add(pat.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
/*        //librerie per commons-lang
        String lib = "C:\\Users\\Dario\\workspace-experimental-object-commons-lang\\commons-lang-3.2\\lib";
        ArrayList<String> libs = new ArrayList<>();

        //get a list of file
        List<File> file = (List<File>) FileUtils.listFiles(new File(lib), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File f : file) {
            libs.add(f.getAbsolutePath());
        }



        libs.add("C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar");
        libs.add("C:\\Program Files\\Java\\jre1.8.0_201\\lib\\rt.jar");
        libs.add("C:\\Program Files\\Java\\jre1.8.0_201\\lib\\jce.jar");
        libs.add("C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar");
        final String[] cls = libs.toArray(new String[0]);

        //   path per commons-lang
        String path = "C:\\Users\\Dario\\workspace-experimental-object-commons-lang";

        //   target per commons-lang
        String[] tgt = {"C:\\Users\\Dario\\workspace-experimental-object-commons-lang\\commons-lang-3.2\\bin"};


        Path dir = FileSystems.getDefault().getPath(path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
            for (Path pat : stream) {
                // Iterate over the paths in the directory and print filenames
                directoryList.add(pat.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        //librerie per commons-math
        ArrayList<String> libs = new ArrayList<>();





        libs.add("C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar");
        libs.add("C:\\Program Files (x86)\\Java\\jre6\\lib\\rt.jar");
        libs.add("C:\\Program Files (x86)\\Java\\jre6\\lib\\jce.jar");
        libs.add("C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar");
        final String[] cls = libs.toArray(new String[0]);
        //   path per commons-lang
        String path = "C:\\Users\\Dario\\workspace-experimental-object-commons-math";

        //   target per commons-lang
        String[] tgt = {"C:\\Users\\Dario\\workspace-experimental-object-commons-math\\commons-math-3.1\\bin"};


        Path dir = FileSystems.getDefault().getPath(path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
            for (Path pat : stream) {
                // Iterate over the paths in the directory and print filenames
                directoryList.add(pat.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Project p = null;
        try {
            LOGGER.debug("Creating call-graph of " + tgt[0]);
            p = new PreviousProject( 4, cls, tgt);
        } catch (NoTestFoundedException | NotDirectoryException e) {
            e.printStackTrace();
        }

        Project finalP = p;
        args = new String[6];

        String[] finalArgs = args;
        directoryList.forEach(paths -> {
            LOGGER.info("Start Analyzing Project: " + paths);
            finalArgs[0] = "-old_target";
            finalArgs[1] = "C:\\Users\\Dario\\.m2\\repository\\org\\hamcrest\\hamcrest-all\\1.3\\hamcrest-all-1.3.jar;C:\\Program Files\\Java\\jre6\\lib\\rt.jar;C:\\Program Files\\Java\\jre6\\lib\\jce.jar;C:\\Users\\Dario\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar";
            finalArgs[2] = "-new_target";
            //target commons-configuration
            //     args[3] = paths + "\\target\\classes;" + paths + "\\target\\test-classes";
            //target per commons-codec/commons-jxpath/commons-io/commons-lang
            finalArgs[3] = paths + "\\bin";
            //target per weka
            //   args[3] = paths + "\\build\\classes;" + paths + "\\build\\testcases";
            //target per commons-dbcp
            // args[3] = paths + "\\target\\classes;" + paths + "\\target\\test-classes;";
            finalArgs[4] = "-old_clsp";
            String c = new String();
            for (String s : libs) {
                c += s + ";";
            }
            finalArgs[5] = c;
            int id = Integer.valueOf(paths.split("_")[1]);

            OptionParser optionParser = new OptionParser(finalArgs);
            try {
                optionParser.parse();
                Project p1 = new NewProject(4, optionParser.getNewProjectVersionClassPath(), optionParser.getNewProjectVersionTarget());
                if (optionParser.getOldProjectVersionOutDir() != null)
                    finalP.saveCallGraph(optionParser.getOldProjectVersionOutDir(), "old");
                if (optionParser.getNewProjectVersionOutDir() != null)
                    p1.saveCallGraph(optionParser.getNewProjectVersionOutDir(), "new");

                OnlyOneGrapMultiThread t = new OnlyOneGrapMultiThread(finalP, p1, optionParser.isAlsoNew());

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

                XMLReport xml = new XMLReport(id, end - start, selected, "RTA");
                xml.writeOut();
            } catch (NoNameException | NoTestFoundedException | NoPathException | NotDirectoryException | ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
            LOGGER.info("Finish Analyzing Project: " + paths);
            System.gc();
        });
    }

    private class comparator implements Comparator {



        @Override
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s = (String) o2;
            if(s1.contains("_") && s.contains("_")){
                int i1 = Integer.valueOf(s1.split("_")[1]);
                int i2 = Integer.valueOf(s.split("_")[1]);
                if(i1>i2){
                    return 1;
                }else if(i1<i2){
                    return -1;
                }else
                    return 0;
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
}
