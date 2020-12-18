package de.majaf.voci.control.service.exceptions;

public class InvalidUserException extends Exception{

    public InvalidUserException(String message) {
        super(message);
    }

    public InvalidUserException() {
        super();
    }

    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }


    public InvalidUserException(Throwable cause) {
        super(cause);
    }
}
