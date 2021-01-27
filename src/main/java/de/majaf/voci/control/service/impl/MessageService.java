package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.service.IMessageService;
import de.majaf.voci.entity.*;
import de.mschoettle.entity.dto.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class MessageService implements IMessageService {

    @Autowired @Qualifier("textChannelService")
    private IChannelService textChannelService;


    @Override
    @Transactional
    public Message createTextMessage(String msg, long textChannelID, User sender) throws ChannelDoesNotExistException {
        TextChannel textChannel = (TextChannel) textChannelService.loadChannelByID(textChannelID);
        Message message = new TextMessage(msg, sender);
        textChannel.addMessage(message);
        textChannelService.saveChannel(textChannel);
        return message;
    }

    @Override
    @Transactional
    public Message createDropsiFileMessage(FileDTO file, long textChannelID, User sender) throws ChannelDoesNotExistException {
        TextChannel textChannel = (TextChannel) textChannelService.loadChannelByID(textChannelID);
        Message message = new DropsiFileMessage(file, sender);
        textChannel.addMessage(message);
        textChannelService.saveChannel(textChannel);
        return message;
    }
}
