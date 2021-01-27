package de.majaf.voci.boundery.contoller.utils;

import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.Message;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * Some utils for communicating with frontend. The methods in this Component are needed by more than one Controller.
 */
@Component
@Scope(value = "singleton")
public class ControllerUtils {

    @Autowired
    private IUserService userService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public User getActiveUser(Authentication auth) throws UserDoesNotExistException {
        if (auth != null)
            return userService.loadUserByID(((User) auth.getPrincipal()).getId());
        else throw new UserDoesNotExistException(0, "User is not logged in.");
    }

    public void authenticateUser(User user, HttpServletRequest req) {
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, ((UserDetails) user).getAuthorities());
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        HttpSession session = req.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
    }

    public void sendSocketEndInvitationMessage(long callID) {
        simpMessagingTemplate.convertAndSend("/broker/" + callID + "/endedInvitation", true);
    }

    public void sendSocketAddInvitationMessage(long invitedID, Call call) {
        simpMessagingTemplate.convertAndSend("/broker/" + invitedID + "/invited", call);
    }

    public void sendSocketEndCallMessage(long callID, boolean endedByTimeout) {
        simpMessagingTemplate.convertAndSend("/broker/" + callID + "/endedCall", endedByTimeout);
    }

    public void sendSocketInitiatorLeftMessage(long callID, User user) {
        simpMessagingTemplate.convertAndSend("/broker/" + callID + "/initiatorLeft", user);
    }

    public void sendSocketMemberLeftMessage(long callID, User user) {
        simpMessagingTemplate.convertAndSend("/broker/" + callID + "/removedCallMember", user);
    }

    public void sendSocketMemberJoinedMessage(long callID, User user) {
        simpMessagingTemplate.convertAndSend("/broker/" + callID + "/addedCallMember", user);
    }

    public void sendSocketDownloadMessage(long textChannelID, long userID, Message message) {
        simpMessagingTemplate.convertAndSend("/broker/" + textChannelID + "/" + userID + "/downloadDropsiFile", message);
    }

    public void sendSocketLeaveCallMessages(User user, Call call, long callID) {
        if (call == null) {
            sendSocketEndInvitationMessage(callID);
        } else {
            if (user instanceof RegisteredUser)
                if (call.getInvitation() != null && call.getInvitation().equals(((RegisteredUser) user).getOwnedInvitation())) {
                    sendSocketInitiatorLeftMessage(callID, user);
                    sendSocketEndInvitationMessage(callID);
                } else {
                    sendSocketMemberLeftMessage(callID, user);
                }
        }
    }

    public void handleUserDoesNotExistException(HttpServletResponse response, UserDoesNotExistException e) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "User with id: " + e.getUserID() + " could not be found");
    }
}
