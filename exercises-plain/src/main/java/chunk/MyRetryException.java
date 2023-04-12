package chunk;

public class MyRetryException extends RuntimeException {

    public MyRetryException(String message) {
        super(message);
    }
}
