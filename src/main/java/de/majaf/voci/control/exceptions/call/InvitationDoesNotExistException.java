package de.majaf.voci.control.exceptions.call;

public class InvitationDoesNotExistException extends Exception{
    
    private long invitationID;

    public InvitationDoesNotExistException() {
        super();
    }

    public InvitationDoesNotExistException(String message) {
        super(message);
    }

    public InvitationDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvitationDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public InvitationDoesNotExistException(long invitationID) {
        this();
        this.invitationID = invitationID;
    }

    public InvitationDoesNotExistException(long invitationID, String message) {
        this(message);
        this.invitationID = invitationID;
    }

    public InvitationDoesNotExistException(long invitationID, String message, Throwable cause) {
        this(message, cause);
        this.invitationID = invitationID;
    }

    public InvitationDoesNotExistException(long invitationID, Throwable cause) {
        this(cause);
        this.invitationID = invitationID;
    }

    public long getInvitationID() {
        return invitationID;
    }

    public void setInvitationID(long invitationID) {
        this.invitationID = invitationID;
    }
}
