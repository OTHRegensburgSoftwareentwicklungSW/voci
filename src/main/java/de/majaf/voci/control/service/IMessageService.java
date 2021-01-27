package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.entity.Message;
import de.majaf.voci.entity.User;
import de.mschoettle.entity.dto.FileDTO;

public interface IMessageService {
    Message createTextMessage(String msg, long channelID, User sender) throws ChannelDoesNotExistException;
    Message createDropsiFileMessage(FileDTO file, long textChannelID, User sender) throws ChannelDoesNotExistException;
}
