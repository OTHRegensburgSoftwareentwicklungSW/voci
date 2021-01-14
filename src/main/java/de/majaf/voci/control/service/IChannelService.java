package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.channel.ChannelIDDoesNotExistException;
import de.majaf.voci.entity.Message;
import de.majaf.voci.entity.TextChannel;
import de.majaf.voci.entity.User;

public interface IChannelService {

    TextChannel createTextChannel();
    TextChannel loadTextChannelByID(long textChannelID) throws ChannelIDDoesNotExistException;
    void deleteTextChannel(TextChannel channel);
    void deleteTextChannelByID(long textChannelID) throws ChannelIDDoesNotExistException;
    Message createTextMessage(String msg, long textChannelID, User user) throws ChannelIDDoesNotExistException;
}
