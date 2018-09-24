package testSelector.exception;

public class NoPathException extends Throwable {
    public NoPathException() {
        super("No path found");
    }

    public NoPathException(String message) {
        super(message);
    }
}
