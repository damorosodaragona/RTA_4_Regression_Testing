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

        b();
        b();
        c();
        if (i) {
            d();
        }
    }

    public void d() {

    }

    public void differenceInPrivateMethod() {
        privateMethodWithChange();
    }

    /*
    public void tryf (){
        differenceInPrivateMethod();
    }
    */

    private void privateMethodWithChange() {

    }

    public void differenceInSignature() {
        methodWithDifferentSignature();
    }

    protected void methodWithDifferentSignature() {
        int k = 4 - 2;

    }

    public int methodWithDifferenceInVariableName() {
        int k = 2;
        return k;

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
        return 6 + 1;
    }

    public final ArrayList<String> differentFinalMethod() {
        ArrayList<String> as = new ArrayList<>();
        as.add("try");
        return as;
    }


    public final static ArrayList<String> differentStaticFinalMethod() {
        int h = 6;
        ArrayList<String> as = new ArrayList<>();

        if (h == 6)
            as.add("try");

        return as;
    }

}
