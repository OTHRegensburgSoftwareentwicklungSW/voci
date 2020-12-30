package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.service.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.service.exceptions.call.InvitationIDDoesNotExistException;
import de.majaf.voci.control.service.exceptions.user.InvalidUserException;
import de.majaf.voci.control.service.exceptions.user.UserIDDoesNotExistException;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import de.majaf.voci.entity.repo.CallRepository;
import de.majaf.voci.entity.repo.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class CallService extends ExternalCallService implements ICallService {

    @Autowired
    private CallRepository callRepo;

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
    public Invitation loadInvitationByID(long invitationID) throws InvitationIDDoesNotExistException {
        return invitationRepo.findById(invitationID).orElseThrow(() -> new InvitationIDDoesNotExistException(invitationID, "Invalid invitationID"));
    }

    @Override
    public void inviteToCall(Invitation invitation, long invitedContactID) throws InvalidUserException, UserIDDoesNotExistException {
        RegisteredUser invited = (RegisteredUser) userService.loadUserByID(invitedContactID);
        if (!invitation.getInitiator().getContacts().contains(invited))
            throw new InvalidUserException(invited, "User not in contacts.");

        invited.addActiveInvitation(invitation);
        invitation.addInvitedUser(invited);
        invitationRepo.save(invitation);
    }

    @Override
    public void removeInvitedUserByID(Invitation invitation, long invitedContactID) throws UserIDDoesNotExistException {
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

    @Override
    @Transactional
    public void startCall(Invitation invitation) {
        Call call = invitation.getCall();
        call.setActive(true);
        call.addParticipant(invitation.getInitiator());
        invitation.getInitiator().setActiveCall(call);
        invitationRepo.save(invitation);
    }

    @Transactional
    @Override
    public void joinCall(RegisteredUser user, Invitation invitation) throws InvalidUserException, InvalidCallStateException {
        if (!invitation.getInvitedUsers().contains(user) && user != invitation.getInitiator()) throw new InvalidUserException(user, "User is not invited");
        Call call = invitation.getCall();
        if (!call.isActive()) throw new InvalidCallStateException(call, "Call is not active");
        user.setActiveCall(invitation.getCall());
        call.addParticipant(user);
        userService.saveUser(user);
    }

    @Transactional
    @Override
    public void leaveCall(RegisteredUser user, Invitation invitation) throws InvalidCallStateException {
        Call call = invitation.getCall();
        call.removeParticipant(user);
        user.setActiveCall(null);
        if (call.getParticipants().isEmpty())
            endCall(invitation);
        invitationRepo.save(invitation);
    }

    @Transactional
    @Override
    public void endCall(User user, Invitation invitation) throws InvalidUserException, InvalidCallStateException {
        if (user != invitation.getInitiator())
            throw new InvalidUserException(user, "User must not end call. No Initiator");
        endCall(invitation);
    }


    private void endCall(Invitation invitation) throws InvalidCallStateException {
        Call call = invitation.getCall();
        if (!call.isActive()) throw new InvalidCallStateException(call, "Call is not active");
        for (User participant : call.getParticipants())
            participant.setActiveCall(null);

        call.removeAllParticipants();
        call.setActive(false);

        for (RegisteredUser invited : invitation.getInvitedUsers())
            invited.removeActiveInvitation(invitation);

        invitation.removeAllInvitedUsers();

        invitationRepo.save(invitation);

    }


}
