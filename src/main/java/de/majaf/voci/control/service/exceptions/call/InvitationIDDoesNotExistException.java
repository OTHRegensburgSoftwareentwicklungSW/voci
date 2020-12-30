package de.majaf.voci.control.service.exceptions.call;

public class InvitationIDDoesNotExistException extends Exception{
    
    private long invitationID;

    public InvitationIDDoesNotExistException() {
        super();
    }

    public InvitationIDDoesNotExistException(String message) {
        super(message);
    }

    public InvitationIDDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvitationIDDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public InvitationIDDoesNotExistException(long invitationID) {
        this();
        this.invitationID = invitationID;
    }

    public InvitationIDDoesNotExistException(long invitationID, String message) {
        this(message);
        this.invitationID = invitationID;
    }

    public InvitationIDDoesNotExistException(long invitationID, String message, Throwable cause) {
        this(message, cause);
        this.invitationID = invitationID;
    }

    public InvitationIDDoesNotExistException(long invitationID, Throwable cause) {
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
