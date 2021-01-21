package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.service.IExternalCallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.exceptions.user.UserTokenDoesNotExistException;
import de.majaf.voci.entity.*;
import de.majaf.voci.entity.repo.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.UUID;

@Service
public class ExternalCallService implements IExternalCallService {

    @Autowired @Qualifier("textChannelService")
    private IChannelService channelService;

    @Autowired
    private InvitationRepository invitationRepo;

    @Autowired
    private IUserService userService;

    @Override
    @Transactional
    public Invitation startCall(RegisteredUser user) throws InvalidCallStateException {
        Invitation invitation = user.getOwnedInvitation();
        Call call = invitation.getCall();
        if(call.isActive()) {
            endCall(invitation);
        }
        call.setTextChannel((TextChannel) channelService.createChannel());
        call.setActive(true);
        call.addParticipant(invitation.getInitiator());
        invitation.getInitiator().setActiveCall(call);
        invitation.setAccessToken(generateAccessToken(invitation));
        invitation.setCreationDate(new Date());
        invitationRepo.save(invitation);
        return invitation;
    }

    private String generateAccessToken(Invitation invitation) {
        return UUID.randomUUID().toString();
    }

    @Override
    @Transactional
    public void endCall(Invitation invitation) throws InvalidCallStateException {
        Call call = invitation.getCall();
        if (!call.isActive()) throw new InvalidCallStateException(call, "Call is not active");
        for (User participant : call.getParticipants())
            participant.setActiveCall(null);

        invitation.setAccessToken(null);
        invitation.setCreationDate(null);
        userService.removeAllGuests(invitation);
        call.removeAllParticipants();
        call.setActive(false);
        channelService.deleteChannel(call.getTextChannel());
        call.setTextChannel(null);

        for (RegisteredUser invited : invitation.getInvitedUsers())
            invited.removeActiveInvitation(invitation);

        invitation.removeAllInvitedUsers();

        invitationRepo.save(invitation);
    }
}
