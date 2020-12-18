package de.majaf.voci.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class TextChannel extends Channel {

    @OneToMany
    private List<Message> messages;
}
