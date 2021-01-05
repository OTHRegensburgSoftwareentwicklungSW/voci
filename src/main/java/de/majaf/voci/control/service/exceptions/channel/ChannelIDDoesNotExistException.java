package de.majaf.voci.control.service.exceptions.channel;

public class ChannelIDDoesNotExistException extends Exception {

    private long channelID;

    public ChannelIDDoesNotExistException() {
        super();
    }

    public ChannelIDDoesNotExistException(String message) {
        super(message);
    }

    public ChannelIDDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelIDDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public ChannelIDDoesNotExistException(long channelID) {
        this();
        this.channelID = channelID;
    }

    public ChannelIDDoesNotExistException(long channelID, String message) {
        this(message);
        this.channelID = channelID;
    }

    public ChannelIDDoesNotExistException(long channelID, String message, Throwable cause) {
        this(message, cause);
        this.channelID = channelID;
    }

    public ChannelIDDoesNotExistException(long channelID, Throwable cause) {
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


