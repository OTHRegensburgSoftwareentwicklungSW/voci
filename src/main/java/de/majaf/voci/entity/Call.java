package de.majaf.voci.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

@Entity

// rename table because call is taken by MYSQL
@Table(name="calls")
public class Call extends SingleIdEntity {

    @OneToMany(mappedBy = "activeCall")
    private List<User> participants;

    @OneToOne(mappedBy = "call")
    private Invitation invitation;

    @OneToOne
    private VoiceChannel voiceChannel;

    @OneToOne
    private TextChannel textChannel;

    public Invitation getInvitation() {
        return invitation;
    }

    public void setInvitation(Invitation invitation) {
        this.invitation = invitation;
    }

    public VoiceChannel getVoiceChannel() {
        return voiceChannel;
    }

    public void setVoiceChannel(VoiceChannel voiceChannel) {
        this.voiceChannel = voiceChannel;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }
}

