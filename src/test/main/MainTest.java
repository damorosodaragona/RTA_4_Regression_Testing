package test.main;

import org.junit.Test;
import testSelector.main.Main;

public class MainTest {


    @Test
    public void main() {
        String[] args1 = {"-old_clsp",
                "D:\\Google Drive Universita\\Università\\Tesi\\p\\java-testing-example-master\\java-testing-example-master\\example\\target\\classes;D:\\Google Drive Universita\\Università\\Tesi\\p\\java-testing-example-master\\java-testing-example-master\\example\\target\\test-classes",
                "-new_clsp",
                "D:\\Google Drive Universita\\Università\\Tesi\\p1\\java-testing-example-master\\java-testing-example-master\\example\\target\\classes;D:\\Google Drive Universita\\Università\\Tesi\\p1\\java-testing-example-master\\java-testing-example-master\\example\\target\\test-classes",
                "-old_out",
                "D:\\Google Drive Universita\\Università\\Tesi",
                "-new_out",
                "D:\\Google Drive Universita\\Università\\Tesi"};

        Main.main(args1);


    }


}
