package de.majaf.voci.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Message extends SingleIdEntity{

    private Date sentAt;
    private /*Object*/ String content;

    @Enumerated(EnumType.ORDINAL)
    private MessageType type;

    @ManyToOne
    private User sender;
}
