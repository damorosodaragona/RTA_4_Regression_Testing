package testSelector.main;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import testSelector.exception.NoPathException;
import testSelector.exception.NoTestFoundedException;
import testSelector.option.OptionParser;
import testSelector.project.Project;
import testSelector.testSelector.TestSelector;


public class Main {

    private static Logger LOGGER = Logger.getLogger(Main.class);
 /*
    public static void main(String[] args) {


        ArrayList<String> option = new ArrayList();
        for (int i = 0; i < args.length; i++) {
            option.add(args[i]);
        }

        if (option.contains("-cgonly")) {
            int path = option.indexOf("-cgonly") + 1;
            try {
                new Project(option.get(path)).saveCallGraph(null);

            } catch (NoTestFoundedException e) {
                LOGGER.severe(e.getMessage() + " for the project passed");
            } catch (NotDirectoryException e) {
                e.printStackTrace();
            } catch (NoPathException noPathExeption) {
                noPathExeption.printStackTrace();
            }

        } else if (option.contains("-st")) {
            int path = option.indexOf("-st") + 1;
            String[] pModulesPaths = new String[100];
            String[] p1ModulesPaths = new String[100];

            if (option.contains("-p")) {
                for (String modulePath : option) {
                    if (modulePath.equals("-p")) {
                        int i = option.indexOf("-p") + 1;
                        pModulesPaths = option.get(i).split(";");

                        p1ModulesPaths = option.get(i + 2).split(";");
                    }
                }
            }
            Project p = null;
            try {
                p = new Project(pModulesPaths);
                p.saveCallGraph(null);
            } catch (NoTestFoundedException e) {
                LOGGER.severe(e.getMessage() + " for '-p' project");
            } catch (NotDirectoryException e) {
                e.printStackTrace();
            } catch (NoPathException noPathExeption) {
                noPathExeption.printStackTrace();
            }
            Project p1 = null;
            try {
                p1 = new Project(p1ModulesPaths);
                p1.saveCallGraph(null);
            } catch (NoTestFoundedException e) {
                LOGGER.severe(e.getMessage() + " for '-p1' project");

            } catch (NotDirectoryException e) {
                e.printStackTrace();
            } catch (NoPathException noPathExeption) {
                noPathExeption.printStackTrace();
            }
            TestSelector t = new TestSelector(p, p1);
            t.selectTest();
            t.getDifferentMethodAndTheirTest().forEach((test, methods) ->
                    {

                        AtomicReference<String> s = new AtomicReference<>(new String());
                        s.set("Will be run test: " + test.getDeclaringClass().getName() + "." + test.getName() + " a cause of change in this methods: ");
                        methods.forEach(method -> {
                            s.set(s.get().concat(System.lineSeparator() + method));
                        });
                        LOGGER.info(s.get());


                    }


            );
            t.runTestMethods();
        }



    }




*/

    public static void main(String[] args) {
        BasicConfigurator.configure();

        OptionParser optionParser = new OptionParser(args);
        try {
            optionParser.parse();
            Project p = new Project(optionParser.getOldProjectVersionclasspath());
            Project p1 = new Project(optionParser.getNewProjectVersionclasspath());

            if (optionParser.getOldProjectVersionclasspath() != null)
                p.saveCallGraph(optionParser.getOldProjectVersionOutDir(), "old");
            if (optionParser.getNewProjectVersionOutDir() != null)
                p1.saveCallGraph(optionParser.getNewProjectVersionOutDir(), "new");

            TestSelector t = new TestSelector(p, p1);
            t.selectTest().forEach(test -> test.runTest());

        } catch (Exception | NoTestFoundedException | NoPathException e) {
            LOGGER.error(e.getMessage());
        }

    }




}
