package test;

import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class sootexampleTestJUnit5 {
    ArrayList<String> a;

    @BeforeEach
    public void init() {
        a = new ArrayList<>();
        a.add("pass");
    }

    @Test
    public void succeedingStandardTest() {
        assertTrue(a.contains("pass"));
    }

    @Test
    public void succeedingGroupedTest() {
        assertAll("a",
                () -> assertTrue(a.contains("pass")),
                () -> assertFalse(a.contains("groupedPass")));

        assertTrue(a.contains("pass"));
    }

    @Test
    public void failingTest() {
        assertTrue(a.contains("fall"));
    }

    @Test
    @Disabled("for demonstration purposes")
    void skippedTest() {
        // not executed
    }

    @AfterEach
    public void tearDown() {
        a.remove("pass");
        ;
    }

    @AfterAll
    public static void tearDownAll() {
        fail("only for demostration");
    }

    @Test
    public void dependentFailAssertion() {
        assertAll("a",
                () -> {
                    assertNotNull(a);
                    assertTrue(a.contains("pass"));
                },
                () -> {
                    assertTrue(a.contains("fail"));
                    assertAll("a",
                            () -> assertNotNull(a.get(2)),
                            () -> assertEquals("fail", a.get(2))
                    );
                }
        );

    }

    @Test
    public void dependentPassAssertion() {
        a.add("fail");
        assertAll("a",
                () -> {
                    assertNotNull(a);
                    assertTrue(a.contains("pass"));
                },
                () -> {
                    assertTrue(a.contains("fail"));
                    assertAll("a",
                            () -> assertNotNull(a.get(1)),
                            () -> assertEquals("fail", a.get(1))
                    );
                }
        );

    }

}
