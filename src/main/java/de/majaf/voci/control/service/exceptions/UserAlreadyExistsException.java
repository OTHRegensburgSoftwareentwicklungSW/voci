package de.majaf.voci.control.service.exceptions;

public class UserAlreadyExistsException extends InvalidUserException{
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException() {
        super();
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }


    public UserAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
