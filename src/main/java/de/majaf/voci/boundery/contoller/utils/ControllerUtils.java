package de.majaf.voci.boundery.contoller.utils;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.GuestUser;
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
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

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

    public void sendSocketLeaveCallMessages(User user, Call call, long callID) {
        if (call == null) {
            simpMessagingTemplate.convertAndSend("/broker/" + callID + "/endedInvitation", true);
        } else {
            if (user instanceof RegisteredUser)
                if (call.getInvitation() != null && call.getInvitation().equals(((RegisteredUser) user).getOwnedInvitation())) {
                    simpMessagingTemplate.convertAndSend("/broker/" + callID + "/initiatorLeft", user);
                    simpMessagingTemplate.convertAndSend("/broker/" + callID + "/endedInvitation", true);
                } else {
                    simpMessagingTemplate.convertAndSend("/broker/" + callID + "/removedCallMember", user);
                }
        }
    }

    public void handleUserDoesNotExistException(HttpServletResponse response, UserDoesNotExistException e) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "User with id: " + e.getUserID() + " could not be found");
    }
}
