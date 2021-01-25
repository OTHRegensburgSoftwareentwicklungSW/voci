package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.call.DropsiException;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.service.IDropsiService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.*;
import de.mschoettle.entity.dto.FileDTO;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.BinaryMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@Scope(value = "singleton")
public class TextChannelController {

    @Autowired
    @Qualifier("textChannelService")
    private IChannelService channelService;

    @Autowired
    private ControllerUtils controllerUtils;

    @MessageMapping("/{textChannelID}/sendMessage")
    @SendTo("/broker/{textChannelID}/receivedMessage")
    public Message sendMessage(@DestinationVariable long textChannelID,
                               String message,
                               Authentication auth) {
        try {
            User user = controllerUtils.getActiveUser(auth);
            return channelService.createTextMessage(message, textChannelID, user);
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
            return channelService.createDropsiFileMessage(file, textChannelID, user);
        } catch (ChannelDoesNotExistException cdnee) {
            return new ErrorMessage("Channel with id: " + cdnee.getChannelID() + " could not be found", HttpServletResponse.SC_NOT_FOUND);
        } catch (UserDoesNotExistException udnee) {
            return new ErrorMessage("User with id: " + udnee.getUserID() + " could not be found", HttpServletResponse.SC_NOT_FOUND);
        }
    }
}