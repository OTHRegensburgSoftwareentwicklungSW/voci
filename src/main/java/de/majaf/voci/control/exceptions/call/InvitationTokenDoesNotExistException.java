package de.majaf.voci.control.exceptions.call;

public class InvitationTokenDoesNotExistException extends Exception{
    private String accessToken;

    public InvitationTokenDoesNotExistException() {
        super();
    }

    public InvitationTokenDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public InvitationTokenDoesNotExistException(String accessToken) {
        this();
        this.accessToken = accessToken;
    }

    public InvitationTokenDoesNotExistException(String accessToken, String message) {
        super(message);
        this.accessToken = accessToken;
    }

    public InvitationTokenDoesNotExistException(String accessToken, String message, Throwable cause) {
        super(message, cause);
        this.accessToken = accessToken;
    }

    public InvitationTokenDoesNotExistException(String accessToken, Throwable cause) {
        this(cause);
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
