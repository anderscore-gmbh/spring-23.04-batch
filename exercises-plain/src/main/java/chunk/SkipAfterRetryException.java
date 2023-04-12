package chunk;

public class SkipAfterRetryException extends RuntimeException {

    public SkipAfterRetryException(String message) {
        super(message);
    }
}
