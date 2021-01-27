package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.InvalidNameException;
import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.exceptions.channel.InvalidChannelException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.entity.*;
import de.mschoettle.entity.dto.FileDTO;

public interface IChannelService {

    Channel saveChannel(Channel channel);
    Channel loadChannelByID(long channelID) throws ChannelDoesNotExistException;
    Channel loadChannelByIDInRoom(long channelID, Room room) throws InvalidUserException, ChannelDoesNotExistException, InvalidChannelException;
    void addChannelToRoom(Room room, String channelName, RegisteredUser initiator) throws InvalidUserException, InvalidNameException;
    void deleteChannelFromRoom(Room room, long channelID, RegisteredUser initiator) throws InvalidUserException, ChannelDoesNotExistException, UserDoesNotExistException;
    void renameChannel(long channelID, Room room, String channelName, RegisteredUser initiator) throws InvalidNameException, InvalidUserException, ChannelDoesNotExistException, InvalidChannelException;
    boolean userIsInChannel(long channelID, User user);
}
