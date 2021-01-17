package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.InvalidNameException;
import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.exceptions.channel.InvalidChannelException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.entity.*;

public interface IChannelService {

    Channel createChannel();
    Channel loadChannelByID(long channelID) throws ChannelDoesNotExistException;
    Channel loadChannelByIDInRoom(long channelID, Room room, RegisteredUser initiator) throws InvalidUserException, ChannelDoesNotExistException, InvalidChannelException;
    void deleteChannel(Channel channel);
    void deleteChannelByID(long channelID) throws ChannelDoesNotExistException;
    Message createTextMessage(String msg, long channelID, User user) throws ChannelDoesNotExistException; // TODO: don't know where to put it
    void addChannelToRoom(Room room, String channelName, RegisteredUser initiator) throws InvalidUserException, InvalidNameException;
    void deleteChannelFromRoom(Room room, long channelID, RegisteredUser initiator) throws InvalidUserException, ChannelDoesNotExistException;
    void renameChannel(long channelID, Room room, String channelName, RegisteredUser initiator) throws InvalidNameException, InvalidUserException, ChannelDoesNotExistException, InvalidChannelException;
}
