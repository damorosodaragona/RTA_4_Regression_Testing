package testselector.exception;

public class NoPathException extends Exception {
    public NoPathException() {
        super("No path found");
    }

    public NoPathException(String msg) {
        super(msg);
    }
}
