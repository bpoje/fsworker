package filesystem.exception;

public class NotEnoughBytesReadException extends Exception {

    public NotEnoughBytesReadException() {}

    public NotEnoughBytesReadException(String message)
    {
       super(message);
    }
}
