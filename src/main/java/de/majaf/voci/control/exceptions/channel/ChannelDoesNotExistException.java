package de.majaf.voci.control.exceptions.channel;

public class ChannelDoesNotExistException extends Exception {

    private long channelID;

    public ChannelDoesNotExistException() {
        super();
    }

    public ChannelDoesNotExistException(String message) {
        super(message);
    }

    public ChannelDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public ChannelDoesNotExistException(long channelID) {
        this();
        this.channelID = channelID;
    }

    public ChannelDoesNotExistException(long channelID, String message) {
        this(message);
        this.channelID = channelID;
    }

    public ChannelDoesNotExistException(long channelID, String message, Throwable cause) {
        this(message, cause);
        this.channelID = channelID;
    }

    public ChannelDoesNotExistException(long channelID, Throwable cause) {
        this(cause);
        this.channelID = channelID;
    }

    public long getChannelID() {
        return channelID;
    }

    public void setChannelID(long channelID) {
        this.channelID = channelID;
    }
}


