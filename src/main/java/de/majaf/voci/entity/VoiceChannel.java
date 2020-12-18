package de.majaf.voci.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class VoiceChannel extends Channel{

    @OneToMany(mappedBy = "activeVoiceChannel")
    private List<User> activeMembers;

}
