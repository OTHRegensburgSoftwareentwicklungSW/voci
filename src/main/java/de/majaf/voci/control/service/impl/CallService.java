package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.service.exceptions.InvalidUserException;
import de.majaf.voci.control.service.exceptions.UserIDDoesNotExistException;
import de.majaf.voci.control.service.exceptions.UsernameDoesNotExistException;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.repo.CallRepository;
import de.majaf.voci.entity.repo.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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


}
