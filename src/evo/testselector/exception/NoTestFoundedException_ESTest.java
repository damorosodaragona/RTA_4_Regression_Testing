/*
 * This file was automatically generated by EvoSuite
 * Tue Oct 09 16:18:05 GMT 2018
 */

package testselector.exception;

import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class)
@EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true)
public class NoTestFoundedException_ESTest extends testselector.exception.NoTestFoundedException_ESTest_scaffolding {

    @Test(timeout = 4000)
    public void test0() throws Throwable {
        NoTestFoundedException noTestFoundedException0 = new NoTestFoundedException();
    }
}
