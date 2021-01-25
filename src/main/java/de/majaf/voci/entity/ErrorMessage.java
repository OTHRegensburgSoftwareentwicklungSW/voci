package de.majaf.voci.entity;

public class ErrorMessage extends Message{

    String message;

    int errorCode;

    public ErrorMessage() {
    }

    public ErrorMessage(String message, int errorCode) {
        super(null);
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
