package de.majaf.voci.entity.wrapper;

public class RegUserInvitationWrapper {

    private long invitationID;
    private String username;

    public RegUserInvitationWrapper() {
    }

    public RegUserInvitationWrapper(long invitationID, String username) {
        this.invitationID = invitationID;
        this.username = username;
    }

    public long getInvitationID() {
        return invitationID;
    }

    public void setInvitationID(long invitationID) {
        this.invitationID = invitationID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
