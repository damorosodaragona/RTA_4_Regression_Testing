package sootTest;

public class SecondClass extends FirstClass {


    @Override
    public int foo(){
        int x = 0;
        for(int i = 0; i< 10; i++){
            x += 89;
        }
        return x;
    }
}
