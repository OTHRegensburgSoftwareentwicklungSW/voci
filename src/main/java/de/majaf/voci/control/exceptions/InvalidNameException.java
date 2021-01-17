package de.majaf.voci.control.exceptions;

public class InvalidNameException extends Exception{
    private String name;

    public InvalidNameException() {
        super();
    }

    public InvalidNameException(Throwable cause) {
        super(cause);
    }

    public InvalidNameException(String name) {
        this();
        this.name = name;
    }

    public InvalidNameException(String name, String message) {
        super(message);
        this.name = name;
    }

    public InvalidNameException(String name, String message, Throwable cause) {
        super(message, cause);
        this.name = name;
    }

    public InvalidNameException(String name, Throwable cause) {
        this(cause);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
