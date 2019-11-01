package testselector.util;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import testselector.main.Main;

import java.lang.reflect.Modifier;


public class Util {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());


   /* private static boolean isJUNIT4TestCase(SootMethod sootMethod) {

        for (Tag t : sootMethod.getTags()) {
            if (t.getClass().equals(VisibilityAnnotationTag.class))
                if (!t.toString().contains("jupiter") && t.toString().contains("junit") && t.toString().contains("Test"))
                    return true;

        }
        SootMethod inheritedMethod = getInheritedMethod(sootMethod);
        if (inheritedMethod != null) return isJUNIT4TestCase(inheritedMethod);
        return false;}*/

    private static boolean isJUNIT3TestCase(SootMethod method) {
        return (method.getName().startsWith("test") && Junit3Condition(method));
    }

    private static boolean Junit3Condition(SootMethod method){
       return ((isAssignableFromJunitTestCaseClass(method.getDeclaringClass())) && (Modifier.isPublic(method.getModifiers()) && (method.getParameterTypes() == null || method.getParameterTypes().isEmpty())));
    }

    //Todo Aggiungere eccezzione.
    private static boolean isAssignableFromJunitTestCaseClass(SootClass clazz) {
        SootClass superClass = null;
        try {
            superClass = clazz.getSuperclass();
        } catch (RuntimeException e) {
            return false;
        }
        String s = null;
        try {
            s = superClass.getName();
            //c'è una superclasse ma soot non la riesce a trovare -> è stato settato male il classpath.
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (s.equals("junit.framework.TestCase"))
            return true;
        else
            return isAssignableFromJunitTestCaseClass(superClass);
    }

    //Todo: un metodo junit4 viene sicuramente riconosciuto come junit5 -> aggiungere testcase
    private static boolean isJunit4or5TestCase(SootMethod sootMethod) {

        for (Tag t : sootMethod.getTags()) {
            if (t.getClass().equals(VisibilityAnnotationTag.class))
                if (t.toString().contains("junit") && t.toString().contains("Test"))
                    return true;

        }
        SootMethod inheritedMethod = getInheritedMethod(sootMethod);
        if (inheritedMethod != null) return isJunit4or5TestCase(inheritedMethod);
        return false;
    }

    /**
     * <pre>
     * This method check if the T object passed is a Junit3/4/5 test method.
     * A method is a Junit3 test method if the method's name starts with "test" and if the class of the methods extend JUnit TestClass
     * A method is a Junit4 test method if the method's is noted with JUnit 4 @Test annotation.
     * A method is a Junit5 test method if the method's is noted with JUnit 5 @Test annotation.
     * @param <T>
     * @param t the method to check. This can be or a Method objcet or a SootMethod object.
     * @param junitVersion
     * @return true if is a JUnit3/4/5 method false if not.
     * </pre>
     */
    public static <T> boolean isJunitTestCase(T t, int junitVersion) {
        if (t.getClass() == SootMethod.class) {
            SootMethod m = (SootMethod) t;
            /*
            if (junitVersion == 3)
                return isJUNIT3TestCase(m);
            else if (junitVersion == 4)
                return isJUNIT4TestCase(m);
            else if (junitVersion == 5)
                return isJunit4or5TestCase(m);*/
            //else
                return isJUNIT3TestCase(m) ||  isJunit4or5TestCase(m);

        }
        return false;
    }

    /**
     * Check if a SootMethod ia a JUnit 3/4/5 Method, so if is noted with @Before, @BeforeClass, @After, @AfterClass or @Test.
     *
     * @param m            the method to check
     * @param junitVersion
     * @return true if is a JUnit 3/4/5 method, false if not.
     */
    public static boolean isATestMethod(SootMethod m, int junitVersion) {
       /* if (!isJunitTestCase(m, junitVersion)) {
            if (junitVersion == 3) {
                return isJunit3TestMethod(m);
            } else if (junitVersion == 4 || junitVersion == 5) {
                return isJunit4or5TestMethod(m, junitVersion);
            } *//*else if (junitVersion == 5) {
                return isJunit5TestMethod(m);
            }*//*
        } else
            return true;*/
        return isJunitTestCase(m, junitVersion) || isJunit3TestMethod(m) || isJunit4or5TestMethod(m, junitVersion);
    }

    private static boolean isJunit4or5TestMethod(SootMethod m, int junitVersion) {
        for (Tag t : m.getTags()) {
            if (t.getClass().equals(VisibilityAnnotationTag.class))
                if (t.toString().contains("junit"))
                    if (isTearDown(m, junitVersion) || isSetup(m, junitVersion))
                        return true;

        }
        SootMethod inheritedMethod = getInheritedMethod(m);
        if (inheritedMethod != null) return isJunit4or5TestMethod(inheritedMethod, junitVersion);
        return false;
    }

   /* private static boolean isJunit5TestMethod(SootMethod m) {
        for (Tag t : m.getTags()) {
            if (t.getClass().equals(VisibilityAnnotationTag.class))
                if (t.toString().contains("junit"))
                    if (isSetup(m, 5) || isTearDown(m, 5))
                        return true;

        }
        SootMethod inheritedMethod = getInheritedMethod(m);
        if (inheritedMethod != null) return isJunit5TestMethod(inheritedMethod);
        return false;
    }
*/
    @Nullable
    private static SootMethod getInheritedMethod(SootMethod m) {
        try {
            SootClass superClass = m.getDeclaringClass().getSuperclass();
            if (superClass != null) {
                SootMethod inheritedMethod = superClass.getMethod(m.getName(), m.getParameterTypes());
                if (inheritedMethod != null)
                    return inheritedMethod;
            }
        } catch (RuntimeException e) {
            return null;
        }
        return null;
    }


    private static boolean isJunit3TestMethod(SootMethod m) {
        return isSetup(m, 3) || isTearDown(m, 3);


    }

    /**
     * Chek if a method is a setUp method or not.
     * A method ia a tear down method in Junit 3 if it's name is equal to "setUp"
     * A method ia a tear down method in Junit 4 if has as tag "Before" or "BeforeClass"
     * A method ia a tear down method in Junit 5 if has as tag "BeforeEach" or "BeforeAll"
     *
     * @param testMethod   the sootMethod to check
     * @param junitVersion the Junit version that you are using
     * @return true if is a setUp method, false otherwise.
     */
    public static boolean isSetup(SootMethod testMethod, int junitVersion) {
        if (junitVersion == 4) {
            for (Tag t : testMethod.getTags()) {
                if (t.getClass().equals(VisibilityAnnotationTag.class))
                    if (t.toString().contains("junit"))
                        if ((!t.toString().contains("BeforeAll") && !t.toString().contains("BeforeEach")) && (t.toString().contains("Before") || t.toString().contains("BeforeClass")))
                            return true;

            }
        }
        if (junitVersion == 3) {
            return testMethod.getName().equals("setUp") && Junit3Condition(testMethod);
        }

        if (junitVersion == 5) {
            for (Tag t : testMethod.getTags()) {
                if (t.getClass().equals(VisibilityAnnotationTag.class))
                    if (t.toString().contains("junit"))
                        if (t.toString().contains("BeforeAll") || t.toString().contains("BeforeEach"))
                            return true;

            }

        }

        SootMethod inheritedMethod = getInheritedMethod(testMethod);
        if (inheritedMethod != null) return isSetup(inheritedMethod, junitVersion);
        return false;
    }

    /**
     * Chek if a method is a tear down method or not.
     * A method ia a tear down method in Junit 3 if it's name is equal to "tearDown"
     * A method ia a tear down method in Junit 4 if has as tag "After" or "AfterClass"
     * A method ia a tear down method in Junit 5 if has as tag "AfterEach" or "AfterAll"
     *
     * @param testMethod   the sootMethod to check
     * @param junitVersion the Junit version that you are using
     * @return true if is a tear donwn method, false otherwise.
     */
    public static boolean isTearDown(SootMethod testMethod, int junitVersion) {
        if (junitVersion == 4) {
            for (Tag t : testMethod.getTags()) {
                if (t.getClass().equals(VisibilityAnnotationTag.class))
                    if (t.toString().contains("junit"))
                        if ((!t.toString().contains("AfterEach") && !t.toString().contains("AfterAll")) && (t.toString().contains("After") || t.toString().contains("AfterClass")))
                            return true;

            }
        }
        if (junitVersion == 3) {
            return testMethod.getName().equals("tearDown") && Junit3Condition(testMethod);
        }
        if (junitVersion == 5) {
            for (Tag t : testMethod.getTags()) {
                if (t.getClass().equals(VisibilityAnnotationTag.class))
                    if (t.toString().contains("junit"))
                        if (t.toString().contains("AfterEach") || t.toString().contains("AfterAll"))
                            return true;

            }
        }

        SootMethod inheritedMethod = getInheritedMethod(testMethod);
        if (inheritedMethod != null) return isTearDown(inheritedMethod, junitVersion);
        return false;
    }
}