package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.Message;
import de.majaf.voci.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TextChannelController {

    @Autowired @Qualifier("textChannelService")
    private IChannelService channelService;

    @Autowired
    private IUserService userService;

    @MessageMapping("/{textChannelID}/{userID}/sendMessage")
    @SendTo("/broker/{textChannelID}/receivedMessage")
    public Message sendMessage(@DestinationVariable long textChannelID, @DestinationVariable long userID, String message) throws UserDoesNotExistException, ChannelDoesNotExistException {
        User user = userService.loadUserByID(userID);
        return channelService.createTextMessage(message, textChannelID, user);
    }

}
