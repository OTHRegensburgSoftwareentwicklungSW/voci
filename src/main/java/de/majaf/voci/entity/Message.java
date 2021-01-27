package de.majaf.voci.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextMessage.class, name = "textMessage"),
        @JsonSubTypes.Type(value = DropsiFileMessage.class, name = "dropsiFileMessage"),
        @JsonSubTypes.Type(value = ErrorMessage.class, name = "errorMessage")})
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
public abstract class Message extends SingleIdEntity implements Comparable<Message>{

    private Date sentAt;

    @ManyToOne
    private User sender;

    public Message() {
        sentAt = new Date();
    }

    public Message(User sender) {
        this.sender = sender;
        sentAt = new Date();
    }

    public Date getSentAt() {
        return sentAt;
    }

    public String getFormatDate() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return df.format(sentAt);
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    // only for thymeleaf
    @JsonIgnore
    public boolean isTextMessage() {
        return this instanceof TextMessage;
    }

    @Override
    public int compareTo(Message msg) {
        return getSentAt().compareTo(msg.getSentAt());
    }
}
