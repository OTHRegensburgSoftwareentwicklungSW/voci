package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IChannelService;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CallService extends ExternalCallService implements ICallService {

    @Autowired
    private CallRepository callRepo;

    @Autowired
    private InvitationRepository invitationRepo;

    @Autowired
    private IUserService userService;

    @Autowired
    private IChannelService channelService;

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
    public Invitation loadInvitationByID(long invitationID) throws InvitationIDDoesNotExistException {
        return invitationRepo.findById(invitationID).orElseThrow(() -> new InvitationIDDoesNotExistException(invitationID, "Invalid invitationID"));
    }

    @Override
    @Transactional
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
    public void startCall(long invitationID) throws InvitationIDDoesNotExistException {
        Invitation invitation = loadInvitationByID(invitationID);
        Call call = invitation.getCall();
        call.setTextChannel(channelService.createTextChannel());
        call.setActive(true);
        call.addParticipant(invitation.getInitiator());
        invitation.getInitiator().setActiveCall(call);
        invitationRepo.save(invitation);
    }

    @Transactional
    @Override
    public void joinCallByInvitationID(RegisteredUser user, long invitationID) throws InvalidUserException, InvalidCallStateException, InvitationIDDoesNotExistException {
        joinCall(user, loadInvitationByID(invitationID));
    }

    @Transactional
    @Override
    public void joinCall(RegisteredUser user, Invitation invitation) throws InvalidUserException, InvalidCallStateException {
        if (!invitation.getInvitedUsers().contains(user) && !user.equals(invitation.getInitiator())) throw new InvalidUserException(user, "User is not invited");
        Call call = invitation.getCall();
        if (!call.isActive()) throw new InvalidCallStateException(call, "Call is not active");
        user.setActiveCall(invitation.getCall());
        call.addParticipant(user);
        userService.saveUser(user);
    }

    @Transactional
    @Override
    public boolean isUserInvited(RegisteredUser user, Invitation invitation) {
        return invitation.getInvitedUsers().contains(user) || user.equals(invitation.getInitiator());
    }

    @Override
    @Transactional
    public boolean isUserInvited(RegisteredUser user, long invitationID) throws InvitationIDDoesNotExistException {
        return isUserInvited(user, loadInvitationByID(invitationID));
    }

    @Override
    @Transactional
    public boolean isUserInCall(User user, long invitationID) throws InvitationIDDoesNotExistException {
        return loadInvitationByID(invitationID).getCall().getParticipants().contains(user);
    }

    @Transactional
    @Override
    public boolean leaveCallByInvitationID(RegisteredUser user, long invitationID) throws InvitationIDDoesNotExistException, InvalidCallStateException {
        return leaveCall(user, loadInvitationByID(invitationID));
    }

    @Transactional
    @Override
    public boolean leaveCall(RegisteredUser user, Invitation invitation) throws InvalidCallStateException {
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
    public void endCallByInvitationID(RegisteredUser user, long invitationID) throws InvalidUserException, InvalidCallStateException, InvitationIDDoesNotExistException {
        Invitation invitation = loadInvitationByID(invitationID);
        if (!user.equals(invitation.getInitiator()))
            throw new InvalidUserException(user, "User must not end call. No Initiator");

        endCall(invitation);
    }


    @Override
    @Transactional
    public void endCall(Invitation invitation) throws InvalidCallStateException {
        Call call = invitation.getCall();
        if (!call.isActive()) throw new InvalidCallStateException(call, "Call is not active");
        for (User participant : call.getParticipants())
            participant.setActiveCall(null);

        call.removeAllParticipants();
        call.setActive(false);
        channelService.deleteTextChannel(call.getTextChannel());
        call.setTextChannel(null);

        for (RegisteredUser invited : invitation.getInvitedUsers())
            invited.removeActiveInvitation(invitation);

        invitation.removeAllInvitedUsers();

        invitationRepo.save(invitation);

    }


}
