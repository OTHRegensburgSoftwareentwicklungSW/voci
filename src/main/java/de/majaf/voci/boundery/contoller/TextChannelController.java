package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.service.exceptions.channel.ChannelIDDoesNotExistException;
import de.majaf.voci.entity.Message;
import de.majaf.voci.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class TextChannelController {

    @Autowired
    private IChannelService channelService;

    @Autowired
    private MainController mainController;

    @MessageMapping("/{textChannelID}/sendMessage")
    @SendTo("/broker/{textChannelID}/receivedMessage")
    public Message sendMessage(@DestinationVariable long textChannelID, String message, Principal principal) {
        User user = mainController.getActiveUser(principal);
        try {
            return channelService.createTextMessage(message, textChannelID, user);
        } catch (ChannelIDDoesNotExistException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
