package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


@Controller
public class InvitationController {

    @Autowired
    private ICallService callService;

    @Autowired
    private IUserService userService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // TODO: Exception Handling
    @RequestMapping(value = "/invitation", method = RequestMethod.GET)
    public String prepareInvitationPage(Model model, @RequestParam("accessToken") Optional<String> accessToken, HttpServletRequest req, Authentication auth) throws InvitationTokenDoesNotExistException, InvalidCallStateException, InvalidUserException, InvitationDoesNotExistException {
        if (accessToken.isEmpty())
            return "invitation";
        else {
            return joinCall(model, accessToken.get(), auth, req);
        }
    }

    @RequestMapping(value = "/invitation", method = RequestMethod.POST)
    public String prepareInvitationAccess(Model model, @ModelAttribute("accessToken") String accessToken, HttpServletRequest req, Authentication auth) throws InvitationTokenDoesNotExistException, InvalidCallStateException, InvalidUserException, InvitationDoesNotExistException {
        return joinCall(model, accessToken, auth, req);
    }

    private String joinCall(Model model, String accessToken, Authentication auth, HttpServletRequest req) throws InvalidCallStateException, InvalidUserException, InvitationTokenDoesNotExistException, InvitationDoesNotExistException {
        User user;
        if (auth == null) {
            System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            user = userService.createGuestUser(accessToken, req);
        } else {
            System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            user = (User) auth.getPrincipal();
            callService.joinCallByAccessToken(user, accessToken);
        }
        Call call = user.getActiveCall();
        simpMessagingTemplate.convertAndSend("/broker/" + call.getInvitation().getId() + "/addedCallMember", user);
        model.addAttribute("user", user);
        model.addAttribute("invitation", call.getInvitation());
        model.addAttribute("textChannel", call.getTextChannel());
        return "call";
    }

    @RequestMapping(value = "/call/ended", method = RequestMethod.GET)
    public String prepareCallEndPage() {
        return "endedCall";
    }

    @RequestMapping(value = "/call/left", method = RequestMethod.GET)
    public String prepareCallLeftPage() {
        return "leftCall";
    }
}
