package de.majaf.voci.control.exceptions.user;

public class UserDoesNotExistException extends Exception{

    private long userID;

    public UserDoesNotExistException() {
        super();
    }

    public UserDoesNotExistException(String message) {
        super(message);
    }

    public UserDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public UserDoesNotExistException(long userID) {
        this();
        this.userID = userID;
    }

    public UserDoesNotExistException(long userID, String message) {
        this(message);
        this.userID = userID;
    }

    public UserDoesNotExistException(long userID, String message, Throwable cause) {
        this(message, cause);
        this.userID = userID;
    }

    public UserDoesNotExistException(long userID, Throwable cause) {
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
