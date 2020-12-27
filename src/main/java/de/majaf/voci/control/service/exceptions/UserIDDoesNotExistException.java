package de.majaf.voci.control.service.exceptions;

import de.majaf.voci.entity.User;

public class UserIDDoesNotExistException extends Exception{

    private long userID;

    public UserIDDoesNotExistException() {
        super();
    }

    public UserIDDoesNotExistException(String message) {
        super(message);
    }

    public UserIDDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserIDDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public UserIDDoesNotExistException(long userID) {
        this();
        this.userID = userID;
    }

    public UserIDDoesNotExistException(long userID, String message) {
        this(message);
        this.userID = userID;
    }

    public UserIDDoesNotExistException(long userID, String message, Throwable cause) {
        this(message, cause);
        this.userID = userID;
    }

    public UserIDDoesNotExistException(long userID, Throwable cause) {
        this(cause);
        this.userID = userID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }
}
