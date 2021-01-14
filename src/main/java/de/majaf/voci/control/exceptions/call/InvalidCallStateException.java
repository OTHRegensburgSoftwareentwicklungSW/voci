package de.majaf.voci.control.exceptions.call;

import de.majaf.voci.entity.Call;

public class InvalidCallStateException extends Exception{

    private Call call;

    public InvalidCallStateException() {
        super();
    }

    public InvalidCallStateException(String message) {
        super(message);
    }

    public InvalidCallStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCallStateException(Throwable cause) {
        super(cause);
    }

    public InvalidCallStateException(Call call) {
        this();
        this.call = call;
    }

    public InvalidCallStateException(Call call, String message) {
        this(message);
        this.call = call;
    }

    public InvalidCallStateException(Call call, String message, Throwable cause) {
        this(message, cause);
        this.call = call;
    }

    public InvalidCallStateException(Call call, Throwable cause) {
        this(cause);
        this.call = call;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }
}
