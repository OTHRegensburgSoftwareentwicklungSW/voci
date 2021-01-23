package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.call.DropsiException;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


@Controller
public class InvitationController {

    @Autowired
    private ICallService callService;

    @Autowired
    private IUserService userService;

    @Autowired
    private DropsiController dropsiController;

    @Autowired
    private MainController mainController;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // TODO: Exception Handling
    @RequestMapping(value = "/invitation", method = RequestMethod.GET)
    public String prepareInvitationPage(Model model, @RequestParam("accessToken") Optional<String> accessToken, HttpServletRequest req, Authentication auth) throws InvitationTokenDoesNotExistException, InvalidCallStateException, InvalidUserException, InvitationDoesNotExistException, UserDoesNotExistException, DropsiException {
        if (accessToken.isEmpty())
            return "invitation";
        else {
            return joinCall(model, accessToken.get(), auth, req);
        }
    }

    @RequestMapping(value = "/invitation", method = RequestMethod.POST)
    public String prepareInvitationAccess(Model model, @ModelAttribute("accessToken") String accessToken, HttpServletRequest req, Authentication auth) throws InvitationTokenDoesNotExistException, InvalidCallStateException, InvalidUserException, InvitationDoesNotExistException, UserDoesNotExistException, DropsiException {
        return joinCall(model, accessToken, auth, req);
    }

    private String joinCall(Model model, String accessToken, Authentication auth, HttpServletRequest req) throws InvalidCallStateException, InvalidUserException, InvitationTokenDoesNotExistException, InvitationDoesNotExistException, UserDoesNotExistException, DropsiException {
        User user;
        if (auth == null) {
            user = userService.createGuestUserAndJoinCall(accessToken, req);
        } else {
            user = mainController.getActiveUser(auth);
            callService.joinCallByAccessToken(user, accessToken);
        }
        Call call = user.getActiveCall();
        simpMessagingTemplate.convertAndSend("/broker/" + call.getInvitation().getId() + "/addedCallMember", user);

        if (user instanceof RegisteredUser)
            dropsiController.addDropsiFilesToModel(model, (RegisteredUser) user);

        model.addAttribute("user", user);
        model.addAttribute("invitation", call.getInvitation());
        model.addAttribute("textChannel", call.getTextChannel());
        return "call";
    }

    @RequestMapping(value = {"/call/left", "/call/ended"}, method = RequestMethod.GET)
    public String prepareCallLeftPage(HttpServletRequest req) throws ServletException {
        req.logout();
        return "leftCall";
    }
}
