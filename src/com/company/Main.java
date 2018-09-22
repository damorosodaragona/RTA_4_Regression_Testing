package com.company;


import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
public class Main {

    private static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        ArrayList<String> option = new ArrayList();
        for (int i = 0; i < args.length; i++) {
            option.add(args[i]);
        }

        if (option.contains("-cgonly")) {
            int path = option.indexOf("-cgonly") + 1;
            try {
                new Project(option.get(path));
            } catch (NoTestFoundedException e) {
                LOGGER.severe(e.getMessage() + " for the project passed");
            } catch (NoPathExeption noPathExeption) {
                LOGGER.severe(noPathExeption.getMessage() + " for the project passed");
            } catch (NotDirectoryException e) {
                LOGGER.severe(e.getMessage() + " for the project passed");
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
            } catch (NoTestFoundedException e) {
                LOGGER.severe(e.getMessage() + " for '-p' project");
            } catch (NoPathExeption noPathExeption) {
                LOGGER.severe(noPathExeption.getMessage() + " for '-p' project");
            } catch (NotDirectoryException e) {
                LOGGER.severe(e.getMessage() + " for '-p' project");
            }
            Project p1 = null;
            try {
                p1 = new Project(p1ModulesPaths);
            } catch (NoTestFoundedException e) {
                LOGGER.severe(e.getMessage() + " for '-p1' project");

            } catch (NoPathExeption noPathExeption) {
                LOGGER.severe(noPathExeption.getMessage() + " for '-p1' project");
            } catch (NotDirectoryException e) {
                LOGGER.severe(e.getMessage() + " for '-p1' project");
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









}
