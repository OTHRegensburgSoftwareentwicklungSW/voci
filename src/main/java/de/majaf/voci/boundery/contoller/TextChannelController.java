package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.service.IMessageService;
import de.majaf.voci.entity.*;
import de.mschoettle.entity.dto.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import javax.servlet.http.HttpServletResponse;

@Controller
@Scope(value = "singleton")
public class TextChannelController {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private ControllerUtils controllerUtils;

    @MessageMapping("/{textChannelID}/sendMessage")
    @SendTo("/broker/{textChannelID}/receivedMessage")
    public Message sendMessage(@DestinationVariable long textChannelID,
                               String message,
                               Authentication auth) {
        try {
            User user = controllerUtils.getActiveUser(auth);
            return messageService.createTextMessage(message, textChannelID, user);
        } catch (ChannelDoesNotExistException cdnee) {
            return new ErrorMessage("Channel with id: " + cdnee.getChannelID() + " could not be found", HttpServletResponse.SC_NOT_FOUND);
        } catch (UserDoesNotExistException udnee) {
            return new ErrorMessage("User with id: " + udnee.getUserID() + " could not be found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @MessageMapping({"/{textChannelID}/sendDropsiFile"})
    @SendTo("/broker/{textChannelID}/receivedMessage")
    public Message sendDropsiFile(@DestinationVariable long textChannelID, FileDTO file, Authentication auth) {
        try {
            RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
            return messageService.createDropsiFileMessage(file, textChannelID, user);
        } catch (ChannelDoesNotExistException cdnee) {
            return new ErrorMessage("Channel with id: " + cdnee.getChannelID() + " could not be found", HttpServletResponse.SC_NOT_FOUND);
        } catch (UserDoesNotExistException udnee) {
            return new ErrorMessage("User with id: " + udnee.getUserID() + " could not be found", HttpServletResponse.SC_NOT_FOUND);
        }
    }
}