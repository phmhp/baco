package solux.baco.exception;

public class NotCorresspondingEmailException extends RuntimeException{
    public NotCorresspondingEmailException() {
    }

    public NotCorresspondingEmailException(String message) {
        super(message);
    }

    public NotCorresspondingEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotCorresspondingEmailException(Throwable cause) {
        super(cause);
    }
}
