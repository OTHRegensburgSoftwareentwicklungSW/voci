package de.majaf.voci.control.exceptions.channel;

import de.majaf.voci.entity.Channel;

public class InvalidChannelException extends Exception{
    private Channel channel;

    public InvalidChannelException() {
        super();
    }

    public InvalidChannelException(String message) {
        super(message);
    }

    public InvalidChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidChannelException(Throwable cause) {
        super(cause);
    }

    public InvalidChannelException(Channel channel) {
        this();
        this.channel = channel;
    }

    public InvalidChannelException(Channel channel, String message) {
        this(message);
        this.channel = channel;
    }

    public InvalidChannelException(Channel channel, String message, Throwable cause) {
        this(message, cause);
        this.channel = channel;
    }

    public InvalidChannelException(Channel channel, Throwable cause) {
        this(cause);
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
