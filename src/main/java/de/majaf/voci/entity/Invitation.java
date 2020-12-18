package de.majaf.voci.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Invitation extends SingleIdEntity{

    private Date creationDate;
    private long timeout;
    private String url;

    @OneToOne
    private RegisteredUser initiator;

    @ManyToMany
    private List<RegisteredUser> invitedUsers;

    @OneToOne
//    @Transient
    private Call call;

//    @OneToMany
    @Transient
    private List<GuestUser> guestUsers;

}
