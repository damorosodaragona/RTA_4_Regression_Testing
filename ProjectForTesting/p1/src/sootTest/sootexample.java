package sootTest;

import java.util.ArrayList;

public class sootexample {

    boolean i = false;

    public void a() {
        i = true;
        c();
    }

    public void b() {
        a();
    }

    public void c() {


        if (i) {
            d();
        } else
            b();
    }

    public void d() {
    }

    public void differenceInPrivateMethod() {
        privateMethodWithChange();
    }

    private void privateMethodWithChange() {
        int i = 2;
        int j = 3;
        int[] g = {i, j};

    }

    public void differenceInSignature() {
        methodWithDifferentSignature();
    }

    private void methodWithDifferentSignature() {
        int k = 4 - 2;
    }

    public int methodWithDifferenceInVariableName() {
        int j = 2;
        return j;
    }

    public void newMethod() {
        new ArrayList<>();
    }

    public ArrayList<String> realMethodToTest() {
        ArrayList a = new ArrayList<>();
        a.add("real");
        a.add("method");
        a.add("toTest");
        return a;
    }

    public static int equalStaticMethod() {
        int h = 6;
        return 6 + 1;
    }

    public final ArrayList<String> equalFinalMethod() {
        ArrayList<String> as = new ArrayList<>();
        as.add("try");
        return as;
    }


    public final static ArrayList<String> equalStaticFinalMethod() {
        int h = 6;
        ArrayList<String> as = new ArrayList<>();

        if (h == 6)
            as.add("try");

        return as;
    }

    public static int differentStaticMethod() {
        int h = 6;
        return h + 1;
    }

    public final ArrayList<String> differentFinalMethod() {
        ArrayList<String> as = new ArrayList<>();
        as.add("tryDifferent");
        return as;
    }


    public final static ArrayList<String> differentStaticFinalMethod() {
        int h = 3;
        ArrayList<String> as = new ArrayList<>();

        if (h == 6)
            as.add("try");

        return as;
    }


}
