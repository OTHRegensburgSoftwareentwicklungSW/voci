package de.majaf.voci.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class TextChannel extends Channel {

    @OneToMany
    private List<Message> messages = new ArrayList<>();

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        if(messages.contains(message))
            messages.add(message);
    }
}
