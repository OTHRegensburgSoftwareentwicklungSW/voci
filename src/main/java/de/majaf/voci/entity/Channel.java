package de.majaf.voci.entity;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Channel extends SingleIdEntity{
    private String channelName;

    public Channel() {
    }

    public Channel(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public abstract boolean isTextChannel();
}
