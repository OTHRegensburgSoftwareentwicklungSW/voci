package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.GuestUser;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@Controller
@Scope("singleton")
public class InvitationController {

    @Autowired
    private ICallService callService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ControllerUtils controllerUtils;

    @Autowired
    private DropsiController dropsiController;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping(value = "/invitation", method = RequestMethod.GET)
    public String prepareInvitationPage(Model model,
                                        @RequestParam("accessToken") Optional<String> accessToken,
                                        HttpServletRequest req,
                                        Authentication auth) throws InvitationTokenDoesNotExistException, InvalidCallStateException, InvalidUserException, InvitationDoesNotExistException, UserDoesNotExistException {
        if (accessToken.isEmpty())
            return "invitation";
        else {
            return joinCall(accessToken.get(), model, req, auth);
        }
    }

    @RequestMapping(value = "/invitation", method = RequestMethod.POST)
    public String prepareInvitationAccess(@ModelAttribute("accessToken") String accessToken,
                                          Model model,
                                          HttpServletRequest req,
                                          Authentication auth) throws InvitationTokenDoesNotExistException, InvitationDoesNotExistException, InvalidCallStateException, InvalidUserException, UserDoesNotExistException {
        return joinCall(accessToken, model, req, auth);
    }

    private String joinCall(String accessToken,
                            Model model,
                            HttpServletRequest req,
                            Authentication auth) throws InvitationTokenDoesNotExistException, InvitationDoesNotExistException, InvalidCallStateException, InvalidUserException, UserDoesNotExistException {
        User user;
        if (auth == null) {
            user = userService.createGuestUserAndJoinCall(accessToken);
            controllerUtils.authenticateUser(user, req);
        } else {
            user = controllerUtils.getActiveUser(auth);

            if (user instanceof GuestUser && user.getActiveCall() != null) {
                Call call = callService.loadCallByToken(accessToken);

                if (!call.equals(user.getActiveCall())) { // when a guestUser is still in a other call, A new guestUser should be created.
                    user = userService.createGuestUserAndJoinCall(accessToken);
                    controllerUtils.authenticateUser(user, req);
                }

            } else
                callService.joinCallByAccessToken(user, accessToken);
        }
        Call call = user.getActiveCall();
        simpMessagingTemplate.convertAndSend("/broker/" + call.getId() + "/addedCallMember", user);

        if (user instanceof RegisteredUser)
            dropsiController.addDropsiFilesToModel(model, (RegisteredUser) user);

        model.addAttribute("user", user);
        model.addAttribute("call", call);
        model.addAttribute("textChannel", call.getTextChannel());
        model.addAttribute("voiceChannel", call.getVoiceChannel());
        return "call";
    }

    @RequestMapping(value = "/call/ended", method = RequestMethod.GET)
    public String prepareCallEndedPage(Model model, HttpServletRequest req, Authentication auth) throws ServletException {
        try {
            if (auth != null) {
                User user = controllerUtils.getActiveUser(auth);
                if (user instanceof RegisteredUser) {
                    model.addAttribute("infoMsg", "Your call has ended. Either it was longer than an hour, or the initiator ended it.");
                    model.addAttribute("user", user);
                    return "prepareCall";
                } else {
                    req.logout();
                    return "leftCall";
                }
            } else return "leftCall";
        } catch (UserDoesNotExistException e) {
            req.logout();
            return "leftCall";
        }
    }

    @ExceptionHandler({InvitationTokenDoesNotExistException.class, InvitationDoesNotExistException.class, InvalidCallStateException.class})
    public String handleInvitationException(Model model) {
        model.addAttribute("errorMsg", "The call or invitation you are looking for does (currently) not exist");
        return "invitation";
    }

    @ExceptionHandler(InvalidUserException.class)
    public String handleInvalidUserException(InvalidUserException e, Model model) {
        model.addAttribute("errorMsg", "User " + e.getUser().getUserName() + " is not invited.");
        return "invitation";
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public void handleUserDoesNotExistException(HttpServletResponse response, UserDoesNotExistException e) throws IOException {
        controllerUtils.handleUserDoesNotExistException(response, e);
    }
}
