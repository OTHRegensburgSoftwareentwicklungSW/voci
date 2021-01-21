package de.majaf.voci.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Message extends SingleIdEntity implements Comparable<Message>{

    private Date sentAt;
    private String content;

    @Enumerated(EnumType.ORDINAL)
    private MessageType type;

    @ManyToOne
    private User sender;

    public Message() {
    }

    public Message(String content, User sender) {
        this.content = content;
        this.sender = sender;
        this.sentAt = new Date();
        this.type = MessageType.TEXT;
    }

    public Date getSentAt() {
        return sentAt;
    }

    @JsonGetter("sentAt") // TODO: maybe do this in thymeleaf
    public String getFormatDate() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return df.format(sentAt);
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "Sender: " + sender.getUserName() + "\nContent: " + content + "\nat: " + getFormatDate();
    }

    @Override
    public int compareTo(Message msg) {
        return getSentAt().compareTo(msg.getSentAt());
    }
}
