package main;


import org.junit.Test;

public class Main {


    private static final String OLD_CLSP = "-old_clsp";
    private static final String OLD_CLSP_VALUE = "D:\\Google Drive Universita\\Università\\Tesi\\p\\java-testing-example-master\\java-testing-example-master\\example\\target\\classes;D:\\Google Drive Universita\\Università\\Tesi\\p\\java-testing-example-master\\java-testing-example-master\\example\\target\\test-classes";
    private static final String NEW_CLSP = "-new_clsp";
    private static final String NEW_CLSP_VALUE = "D:\\Google Drive Universita\\Università\\Tesi\\p1\\java-testing-example-master\\java-testing-example-master\\example\\target\\classes;D:\\Google Drive Universita\\Università\\Tesi\\p1\\java-testing-example-master\\java-testing-example-master\\example\\target\\test-classes";
    private static final String OLD_DIR_OUT = "-old_out";
    private static final String DIR_OUT_VALUE = "D:\\Google Drive Universita\\Università\\Tesi";
    private static final String NEW_DIR_OUT = "-new_out";

    @Test
    public void main() {
        String[] args1 = {OLD_CLSP,
                OLD_CLSP_VALUE,
                NEW_CLSP,
                NEW_CLSP_VALUE,
                OLD_DIR_OUT,
                DIR_OUT_VALUE,
                NEW_DIR_OUT,
                DIR_OUT_VALUE};

        testselector.main.Main.main(args1);


    }

    @Test
    public void mainNoPathException() throws Exception {
        String[] args1 = new String[]{OLD_CLSP,
                NEW_CLSP,
                NEW_CLSP_VALUE,
                OLD_DIR_OUT,
                DIR_OUT_VALUE,
                NEW_DIR_OUT,
                DIR_OUT_VALUE};


        testselector.main.Main.main(args1);
    }

    @Test
    public void mainNullNoPathException() {
        String[] args1 = {OLD_CLSP,
                NEW_CLSP,
                "",
                OLD_DIR_OUT,
                DIR_OUT_VALUE,
                NEW_DIR_OUT,
                DIR_OUT_VALUE};

        testselector.main.Main.main(args1);
    }

    @Test
    public void mainNoTestFoundedException() {
        String[] args1 = {OLD_CLSP,
                OLD_CLSP_VALUE,
                NEW_CLSP,
                "com.company",
                OLD_DIR_OUT,
                DIR_OUT_VALUE,
                NEW_DIR_OUT,
                DIR_OUT_VALUE};

        testselector.main.Main.main(args1);
    }


}
