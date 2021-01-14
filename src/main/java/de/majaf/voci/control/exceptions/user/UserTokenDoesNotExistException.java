package de.majaf.voci.control.exceptions.user;

public class UserTokenDoesNotExistException extends Exception{
    private String userToken;

    public UserTokenDoesNotExistException() {
        super();
    }

    public UserTokenDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public UserTokenDoesNotExistException(String userID) {
        this();
        this.userToken = userID;
    }

    public UserTokenDoesNotExistException(String userID, String message) {
        this(message);
        this.userToken = userID;
    }

    public UserTokenDoesNotExistException(String userID, String message, Throwable cause) {
        this(message, cause);
        this.userToken = userID;
    }

    public UserTokenDoesNotExistException(String userID, Throwable cause) {
        this(cause);
        this.userToken = userID;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
