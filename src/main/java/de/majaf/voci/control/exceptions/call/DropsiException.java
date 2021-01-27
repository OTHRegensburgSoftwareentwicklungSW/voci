package de.majaf.voci.control.exceptions.call;

public class DropsiException extends Exception{

    public DropsiException(String message) {
        super(message);
    }

    public DropsiException(String message, Throwable cause) {
        super(message, cause);
    }

    public DropsiException(Throwable cause) {
        super(cause);
    }
}
