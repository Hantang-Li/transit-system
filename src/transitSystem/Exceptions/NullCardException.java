package transitSystem.Exceptions;

/** Exception raised when unable to find the card*/
public class NullCardException extends NullPointerException{
    /** Construct this exception*/
    public NullCardException(String message) {
        super(message);
    }
}
