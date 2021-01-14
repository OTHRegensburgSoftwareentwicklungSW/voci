package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.user.UserIDDoesNotExistException;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.exceptions.channel.ChannelIDDoesNotExistException;
import de.majaf.voci.control.service.IUserService;
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
    private IUserService userService;

    @Autowired
    private MainController mainController;

    @MessageMapping("/{textChannelID}/{userID}/sendMessage")
    @SendTo("/broker/{textChannelID}/receivedMessage")
    public Message sendMessage(@DestinationVariable long textChannelID, @DestinationVariable long userID, String message) throws UserIDDoesNotExistException, ChannelIDDoesNotExistException {
        User user = userService.loadUserByID(userID);
        return channelService.createTextMessage(message, textChannelID, user);

    }

}
