package sootTest;

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
}
