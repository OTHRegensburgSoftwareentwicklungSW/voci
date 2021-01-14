package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.entity.GuestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class InvitationController {

    @Autowired
    private ICallService callService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // TODO: Exception Handling
    @RequestMapping(value = "/invitation/{accessToken}")
    public String takeInvitation(Model model, @PathVariable String accessToken) throws InvitationTokenDoesNotExistException, InvalidUserException, InvalidCallStateException {
        GuestUser user = callService.createGuestUserAndJoinCall(accessToken);
        simpMessagingTemplate.convertAndSend("/broker/"+ user.getActiveCall().getInvitation().getId() + "/addedCallMember", user.getUserName());
        model.addAttribute("user", user);
        model.addAttribute("invitation", user.getActiveCall().getInvitation());
        model.addAttribute("textChannel", user.getActiveCall().getTextChannel());
        return "call";
    }
}
