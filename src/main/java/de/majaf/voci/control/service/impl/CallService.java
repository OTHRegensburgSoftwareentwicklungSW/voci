package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.service.IExternalCallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.entity.*;
import de.majaf.voci.entity.repo.CallRepository;
import de.majaf.voci.entity.repo.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CallService extends ExternalCallService implements ICallService {

    @Autowired
    private InvitationRepository invitationRepo;

    @Autowired
    private IUserService userService;

    @Override
    @Transactional
    public Invitation createInvitation(RegisteredUser initiator) {
        Call call = new Call();
        Invitation invitation = new Invitation(initiator, call);
        call.setInvitation(invitation);
        initiator.setOwnedInvitation(invitation);
        invitationRepo.save(invitation);
        return invitation;
    }

    @Override
    @Transactional
    public Invitation loadInvitationByID(long id) throws InvitationDoesNotExistException {
        return invitationRepo.findById(id).orElseThrow(() -> new InvitationDoesNotExistException(id, "Invalid Invitation-ID"));
    }

    @Override
    public Invitation saveInvitation(Invitation invitation) {
        return invitationRepo.save(invitation);
    }

    @Override
    @Transactional
    public void inviteToCall(Invitation invitation, long invitedContactID) throws InvalidUserException, UserDoesNotExistException {
        RegisteredUser invited = (RegisteredUser) userService.loadUserByID(invitedContactID);
        if (!invitation.getInitiator().getContacts().contains(invited))
            throw new InvalidUserException(invited, "User not in contacts.");

        invited.addActiveInvitation(invitation);
        invitation.addInvitedUser(invited);
        invitationRepo.save(invitation);
    }

    @Override
    public void removeInvitedUserByID(Invitation invitation, long invitedContactID) throws UserDoesNotExistException {
        RegisteredUser invited = (RegisteredUser) userService.loadUserByID(invitedContactID);
        removeInvitedUser(invitation, invited);
    }

    @Transactional
    @Override
    public void removeInvitedUser(Invitation invitation, RegisteredUser invited) {
        invited.removeActiveInvitation(invitation);
        invitation.removeInvitedUser(invited);
        invitationRepo.save(invitation);
    }

    @Transactional
    @Override
    public void joinCallByInvitationID(User user, long invitationID) throws InvalidUserException, InvalidCallStateException, InvitationDoesNotExistException {
        joinCall(user, loadInvitationByID(invitationID));
    }

    @Transactional
    @Override
    public void joinCallByAccessToken(User user, String accessToken) throws InvalidUserException, InvalidCallStateException, InvitationTokenDoesNotExistException {
        joinCall(user, loadInvitationByToken(accessToken));
    }

    @Transactional
    @Override
    public void joinCall(User user, Invitation invitation) throws InvalidUserException, InvalidCallStateException {
        if (user instanceof RegisteredUser)
            if (!invitation.getInvitedUsers().contains(user) && !user.equals(invitation.getInitiator()))
                throw new InvalidUserException(user, "User is not invited");
        Call call = invitation.getCall();
        if (!call.isActive()) throw new InvalidCallStateException(call, "Call is not active");
        user.setActiveCall(invitation.getCall());
        call.addParticipant(user);
        userService.saveUser(user);
    }

    @Transactional
    @Override
    public boolean leaveCallByInvitationID(User user, long invitationID) throws InvitationDoesNotExistException, InvalidCallStateException {
        return leaveCall(user, loadInvitationByID(invitationID));
    }

    @Transactional
    @Override
    public boolean leaveCall(User user, Invitation invitation) throws InvalidCallStateException {
        Call call = invitation.getCall();
        call.removeParticipant(user);
        user.setActiveCall(null);
        if (call.getParticipants().isEmpty())
            endCall(invitation);

        userService.saveUser(user);
        invitationRepo.save(invitation);
        return call.isActive();
    }

    @Override
    @Transactional
    public void endCallByInvitationID(RegisteredUser user, long invitationID) throws InvalidUserException, InvalidCallStateException, InvitationDoesNotExistException {
        Invitation invitation = loadInvitationByID(invitationID);
        if (!user.equals(invitation.getInitiator()))
            throw new InvalidUserException(user, "User must not end call. No Initiator");

        endCall(invitation);
    }
}
