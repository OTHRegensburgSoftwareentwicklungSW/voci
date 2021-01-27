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
import java.util.logging.Logger;

@Service
@Scope("singleton")
public class ExternalCallService implements IExternalCallService {

    @Autowired
    private InvitationRepository invitationRepo;

    @Autowired
    private CallRepository callRepo;

    @Autowired
    @Qualifier("textChannelService")
    private IChannelService textChannelService;

    @Autowired
    @Qualifier("voiceChannelService")
    private IChannelService voiceChannelService;

    @Autowired
    private IUserService userService;

    @Autowired
    private Logger logger;

    @Override
    @Transactional
    public Invitation loadInvitationByToken(String accessToken) throws InvitationTokenDoesNotExistException {
        return invitationRepo.findByAccessToken(accessToken).orElseThrow(() -> new InvitationTokenDoesNotExistException(accessToken, "Invalid Access-Token"));
    }

    @Override
    @Transactional
    public Call loadCallByToken(String accessToken) throws InvitationTokenDoesNotExistException, InvalidCallStateException {
        Invitation invitation = loadInvitationByToken(accessToken);
        Call call = invitation.getCall();
        if (call != null)
            return call;
        else throw new InvalidCallStateException(call, "Call for this Token does not exist");
    }

    private String generateAccessToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    @Transactional
    public Call startCall(RegisteredUser initiator) {
        Invitation invitation = initiator.getOwnedInvitation();

        Call call = new Call();
        call.setCreationDate(new Date());

        call.setInvitation(invitation);
        invitation.setCall(call);

        call.setTextChannel(new TextChannel());

        VoiceChannel voiceChannel = new VoiceChannel();
        voiceChannel.addActiveMember(initiator);
        initiator.setActiveVoiceChannel(voiceChannel);
        call.setVoiceChannel(voiceChannel);

        call.addParticipant(initiator);
        initiator.setActiveCall(call);

        call = callRepo.save(call);

        invitation.setAccessToken(generateAccessToken());
        invitationRepo.save(invitation);

        logger.info("User " + initiator.getUserName() + " started a call.");
        return call;
    }

    @Override
    @Transactional
    public void endCall(Call call) {
        if (call != null) {
            logger.info("Ending call with id: " + call.getId() + ". Remaining participants: " + call.getParticipants().size());

            for (User participant : call.getParticipants()) {
                participant.setActiveCall(null);
                participant.setActiveVoiceChannel(null);
                userService.saveUser(participant);
            }

            Invitation invitation = call.getInvitation();

            if (invitation != null) {
                endInvitation(invitation);
            }

            call.setVoiceChannel(null); // Through orphan-removal the voice-channel reference is also deleted in User-object
                                        // (without orphan-removal the call.setVoiceChannel(null) would not be needed,
                                        // because call is deleted anyway when call is deleted (cascadeType.ALL),
                                        // but user.setVoiceChannel(null) would be needed for each participant)
            callRepo.delete(call);
        }
    }

    @Override
    public void endCallAuthenticated(Call call, User user) throws InvalidUserException {
        if (call != null && call.getInvitation() != null)
            if (user instanceof RegisteredUser &&
                    call.getInvitation().equals(((RegisteredUser) user).getOwnedInvitation()))
                endCall(call);
            else throw new InvalidUserException(user, "User must not end call. No initiator.");
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
        logger.info("Invitation from " + invitation.getInitiator().getUserName() + " ended.");
    }
}
