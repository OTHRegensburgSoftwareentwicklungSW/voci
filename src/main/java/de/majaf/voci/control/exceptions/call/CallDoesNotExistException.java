package de.majaf.voci.control.exceptions.call;

// TODO: remove or use
public class CallDoesNotExistException extends Exception{
    private long callID;

    public CallDoesNotExistException() {
        super();
    }

    public CallDoesNotExistException(String message) {
        super(message);
    }

    public CallDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public CallDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public CallDoesNotExistException(long callID) {
        this();
        this.callID = callID;
    }

    public CallDoesNotExistException(long callID, String message) {
        this(message);
        this.callID = callID;
    }

    public CallDoesNotExistException(long callID, String message, Throwable cause) {
        this(message, cause);
        this.callID = callID;
    }

    public CallDoesNotExistException(long callID, Throwable cause) {
        this(cause);
        this.callID = callID;
    }

    public long getCallID() {
        return callID;
    }

    public void setCallID(long callID) {
        this.callID = callID;
    }
}
