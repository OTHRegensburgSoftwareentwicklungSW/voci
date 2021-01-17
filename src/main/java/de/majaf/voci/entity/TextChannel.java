package de.majaf.voci.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class TextChannel extends Channel {

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "text_channel_id")
    private List<Message> messages = new ArrayList<>();

    public TextChannel() {
    }

    public TextChannel(String channelName) {
        super(channelName);
    }

    public List<Message> getMessages() {
        Collections.sort(messages);
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(Message message) {
        if (!messages.contains(message))
            messages.add(message);
    }

    @Override
    public boolean isTextChannel() {
        return true;
    }
}
