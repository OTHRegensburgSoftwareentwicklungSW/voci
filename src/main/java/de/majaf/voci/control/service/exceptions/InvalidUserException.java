package de.majaf.voci.control.service.exceptions;

import de.majaf.voci.entity.User;

public class InvalidUserException extends Exception {

    private User user;

    public InvalidUserException() {
        super();
    }

    public InvalidUserException(String message) {
        super(message);
    }

    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserException(Throwable cause) {
        super(cause);
    }

    public InvalidUserException(User user) {
        this();
        this.user = user;
    }

    public InvalidUserException(User user, String message) {
        this(message);
        this.user = user;
    }

    public InvalidUserException(User user, String message, Throwable cause) {
        this(message, cause);
        this.user = user;
    }

    public InvalidUserException(User user, Throwable cause) {
        this(cause);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
