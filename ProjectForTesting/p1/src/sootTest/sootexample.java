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
}
