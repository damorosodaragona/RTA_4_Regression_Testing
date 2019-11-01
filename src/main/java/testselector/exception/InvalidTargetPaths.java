package testselector.exception;

public class InvalidTargetPaths extends Exception {
   public InvalidTargetPaths(){
        super("The path for target classes can't be null");
    }
}
