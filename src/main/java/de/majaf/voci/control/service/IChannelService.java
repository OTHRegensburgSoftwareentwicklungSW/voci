package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.InvalidNameException;
import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.exceptions.channel.InvalidChannelException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.entity.*;
import de.mschoettle.entity.dto.FileDTO;

/**
 * This service interface is implemented by two Service-classes {@link de.majaf.voci.control.service.impl.TextChannelService} and
 * {@link de.majaf.voci.control.service.impl.VoiceChannelService}.
 *
 * The design-decision was to provide the same managing methods for both types, but to store them in different Repositories,
 * because the key-functionality of the both channels is different. (Voice-Streaming vs Text-Messaging.) Also attributes differ.
 * Key functionality is mainly handled in {@link IMessageService} for TextChannel.
 *
 * If Voice-Streaming would be added some time it would also be easier to add the functionality with a new Voice-Streaming-Service.#
 *
 * I must admit, that some code in the interface implementations are duplicated. But I can fulfill the requirement of decision making for injection of multiple Bean-types.
 * If this requirement did not exist, I would probably handled this differently
 *
 */
public interface IChannelService {

    /**
     * Saves a channel to database
     * @param channel channel, which is to be saved
     * @return saved channel
     */
    Channel saveChannel(Channel channel);


    /**
     * Loads a channel from the database.
     * @param channelID id from channel, which should be loaded
     * @return loaded channel
     * @throws ChannelDoesNotExistException if channel with this id does not exist in database
     */
    Channel loadChannelByID(long channelID) throws ChannelDoesNotExistException;


    /**
     * Loads a channel from the database, if it is in room.
     * @param channelID id from channel, which should be loaded
     * @param room room, in which the channel should be
     * @return Channel loaded channel
     * @throws ChannelDoesNotExistException if the channel with this id does not exist in database
     * @throws InvalidChannelException if the channel with this is is not in room
     */
    Channel loadChannelByIDInRoom(long channelID, Room room) throws ChannelDoesNotExistException, InvalidChannelException;


    /**
     * Creates a room and adds it to room.
     * @param room room, to which the channel should be added
     * @param channelName name of the channel
     * @param initiator user, who initiates adding channel
     * @throws InvalidUserException if user is not owner of room
     * @throws InvalidNameException if channelName is empty or too long
     */
    void addChannelToRoom(Room room, String channelName, RegisteredUser initiator) throws InvalidUserException, InvalidNameException;


    /**
     * Deletes a room from database and removes it from room
     * @param room room, from which the channel should be deleted
     * @param channelID id of channel
     * @param initiator user, who initiates deleting channel
     * @throws InvalidUserException if user is not owner of room
     * @throws ChannelDoesNotExistException if channel with this id does not exist in the database
     */
    void deleteChannelFromRoom(Room room, long channelID, RegisteredUser initiator)
            throws InvalidUserException, ChannelDoesNotExistException;


    /**
     * Sets the name of a channel to a new value.
     *
     * @param channelID id of channel
     * @param room room, in which the channel should be renamed
     * @param channelName new name for the channel
     * @param initiator  user, who initiates renaming channel
     * @throws InvalidNameException if channelName is empty or too long
     * @throws InvalidUserException if user is not owner of room
     * @throws ChannelDoesNotExistException if channel with this id does not exist in the database
     * @throws InvalidChannelException if this channel is not in the room
     */
    void renameChannel(long channelID, Room room, String channelName, RegisteredUser initiator)
            throws InvalidNameException, InvalidUserException, ChannelDoesNotExistException, InvalidChannelException;


    /**
     * Checks if a user is active in a channel or has access to it.
     * @param channelID id of channel
     * @param user user, who is to be checked if in channel.
     * @return if user is in channel
     */
    boolean userIsInChannel(long channelID, User user);
}
