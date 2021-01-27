package de.majaf.voci.boundery.security;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Logout-Handler for cleaning up active {@link User} when he logs out.
 */
@Component
public class CustomLogoutHandler implements LogoutHandler {

    @Autowired
    private ControllerUtils controllerUtils;

    @Autowired
    private ICallService callService;

    @Autowired
    private IUserService userService;

    /**
     * Leaves {@link Call} or {@link de.majaf.voci.entity.VoiceChannel} if {@link User} is still active in one
     */
    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        try {
            User user = controllerUtils.getActiveUser(authentication);
            if(user.getActiveCall() != null) {
                long callID = user.getActiveCall().getId();
                Call call = callService.leaveCall(user);
                controllerUtils.sendSocketLeaveCallMessages(user, call, callID);
            }

            if (user instanceof RegisteredUser) {
                if (user.getActiveVoiceChannel() != null) {
                    userService.leaveVoiceChannel(user);
                }
            }
        } catch (UserDoesNotExistException userDoesNotExistException) {
            // can be ignored, because User should be logged out anyway.
        } catch (InvalidCallStateException e) {
            // can be ignored, because it can not occur (null-check for Call)
        }
    }
}
