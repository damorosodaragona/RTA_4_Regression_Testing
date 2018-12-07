/*
 * This file was automatically generated by EvoSuite
 * Tue Oct 09 16:17:17 GMT 2018
 */

package testselector.main;

import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.fail;

@RunWith(EvoRunner.class)
@EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true)
public class Main_ESTest extends Main_ESTest_scaffolding {

    @Test(timeout = 4000)
    public void test0() throws Throwable {
        Main main0 = new Main();
        String[] stringArray0 = new String[3];
        stringArray0[0] = "A%sJ]C}HCFMkFeV";
        stringArray0[1] = "";
        stringArray0[2] = "-TOUu,Tv5{";
        Main.main(stringArray0);
        Main.main(stringArray0);
    }

    @Test(timeout = 4000)
    public void test1() throws Throwable {
        String[] stringArray0 = null;
        // Undeclared exception!
        try {
            Main.main((String[]) null);
            fail("Expecting exception: NullPointerException");

        } catch (NullPointerException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            verifyException("testselector.option.OptionParser", e);
        }
    }

    @Test(timeout = 4000)
    public void test2() throws Throwable {
        String[] stringArray0 = new String[0];
        // Undeclared exception!
        try {
            Main.main(stringArray0);
            fail("Expecting exception: NullPointerException");

        } catch (NullPointerException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            verifyException("testselector.option.OptionParser", e);
        }
    }

    @Test(timeout = 4000)
    public void test3() throws Throwable {
        Main main0 = new Main();
        String[] stringArray0 = new String[10];
        // Undeclared exception!
        try {
            Main.main(stringArray0);
            fail("Expecting exception: NullPointerException");

        } catch (NullPointerException e) {
            //
            // no message in exception (getMessage() returned null)
            //
        }
    }
}
