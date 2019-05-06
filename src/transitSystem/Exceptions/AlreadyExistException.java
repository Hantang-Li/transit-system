package transitSystem.Exceptions;
/** Exception raised when adding something that already exist and may cause override*/
public class AlreadyExistException extends Exception{
    /** Construct AlreadyExistException exception*/
    public AlreadyExistException(String message) {
        super(message);
    }
}