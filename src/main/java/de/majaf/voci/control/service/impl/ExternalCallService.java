package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.service.IExternalCallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.*;
import de.majaf.voci.entity.repo.CallRepository;
import de.majaf.voci.entity.repo.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.UUID;

@Service
@Scope("session")
public class ExternalCallService implements IExternalCallService {

    @Autowired
    private InvitationRepository invitationRepo;

    @Autowired
    private CallRepository callRepo;

    @Autowired
    @Qualifier("textChannelService")
    private IChannelService channelService;

    @Autowired
    private IUserService userService;

    @Override
    @Transactional
    public Invitation loadInvitationByToken(String accessToken) throws InvitationTokenDoesNotExistException {
        return invitationRepo.findByAccessToken(accessToken).orElseThrow(() -> new InvitationTokenDoesNotExistException(accessToken, "Invalid Access-Token"));
    }

    private String generateAccessToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    @Transactional
    public Call startCall(RegisteredUser user) {
        Invitation invitation = user.getOwnedInvitation();

        Call call = new Call();
        call.setCreationDate(new Date());
        call.setInvitation(invitation);
        invitation.setCall(call);
        call.setTextChannel((TextChannel) channelService.createChannel());
        call.addParticipant(invitation.getInitiator());
        invitation.getInitiator().setActiveCall(call);
        call = callRepo.save(call);

        invitation.setAccessToken(generateAccessToken());
        invitationRepo.save(invitation);
        return call;
    }

    @Override
    @Transactional
    public void endCallByAccessToken(RegisteredUser user, String accessToken) throws InvalidUserException, InvitationTokenDoesNotExistException {
        Invitation invitation = loadInvitationByToken(accessToken);
        if (!user.equals(invitation.getInitiator()))
            throw new InvalidUserException(user, "User must not end call. No Initiator");

        endCall(invitation.getCall());
    }

    @Override
    @Transactional
    public void endCall(Call call) {
        if (call != null) {

            for (User participant : call.getParticipants())
                participant.setActiveCall(null);

            Invitation invitation = call.getInvitation();

            if (invitation != null)
                endInvitation(invitation);

            userService.removeAllGuests(call);

            callRepo.delete(call);
        }
    }

    @Transactional
    @Override
    public void endInvitation(Invitation invitation) {
        invitation.setAccessToken(null);
        invitation.setCall(null);
        for (RegisteredUser invited : invitation.getInvitedUsers())
            invited.removeActiveInvitation(invitation);
        invitation.removeAllInvitedUsers();
        invitationRepo.save(invitation);
    }
}
