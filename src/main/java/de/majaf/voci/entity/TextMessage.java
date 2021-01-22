package de.majaf.voci.entity;

import javax.persistence.Entity;

@Entity
public class TextMessage extends Message{

    private String content;

    public TextMessage() {
    }

    public TextMessage(String content, User sender) {
        super(sender);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Sender: " + getSender().getUserName() + "\nContent: " + content + "\nat: " + getFormatDate();
    }
}
