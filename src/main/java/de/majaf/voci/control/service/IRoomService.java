package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.InvalidNameException;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.exceptions.channel.InvalidChannelException;
import de.majaf.voci.control.exceptions.room.RoomDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.entity.*;

import javax.transaction.Transactional;

/**
 * Service-interface for managing rooms and its components(members, channels)
 */
public interface IRoomService {

    /**
     * Persists a room in the database
     * @param room room, which should be persisted
     * @return saved room
     */
    Room saveRoom(Room room);


    /**
     * Loads a room from the database
     * @param id id from room, which should be loaded
     * @return loaded room
     * @throws RoomDoesNotExistException if a room with this id is not in the database
     */
    Room loadRoomByID(long id) throws RoomDoesNotExistException;


    /**
     * Creates room with owner as a member and saves it in database
     * @param roomName name of the room
     * @param owner initiatior of the room-creation
     * @throws InvalidNameException if the name is empty or too long
     */
    void createRoom(String roomName, RegisteredUser owner) throws InvalidNameException;


    /**
     * Adds a user to the rooms members
     * @param room room, which is to be updated
     * @param memberID id of the user, who is to be added to members
     * @param initiator user who initiated to add a new member
     * @throws InvalidUserException if initiator is not a member of the room
     * @throws UserDoesNotExistException if the memberID matches no user in the database
     */
    void addMemberToRoom(Room room, long memberID, RegisteredUser initiator) throws InvalidUserException, UserDoesNotExistException;


    /**
     * Removes a user from the rooms members
     * @param room room, which is to be updated
     * @param memberID id of the user, who is to be removed from members
     * @param initiator user who initiated to add a new member
     * @throws InvalidUserException if initiator is not owner of the room
     * @throws UserDoesNotExistException if the memberID matches no user in the database
     */
    void removeMemberFromRoom(Room room, long memberID, RegisteredUser initiator) throws InvalidUserException, UserDoesNotExistException;


    /**
     * Removes a user from the rooms members
     * @param roomID id of the room, where the member wants to leave
     * @param member user, who is to be removed from room-members
     * @throws RoomDoesNotExistException if the roomID matches no room in the database
     * @throws InvalidUserException if the user is owner of the room
     */
    void leaveRoom(long roomID, RegisteredUser member) throws RoomDoesNotExistException, InvalidUserException;


    /**
     * Deletes a room
     * @param roomID id of the room, which is to be deleted
     * @param initiator user who initiates to delete the room
     * @throws RoomDoesNotExistException if the roomID matches no room in the database
     * @throws InvalidUserException if the user in not owner of the room
     */
    void deleteRoom(long roomID, RegisteredUser initiator) throws RoomDoesNotExistException, InvalidUserException;


    /**
     * Checks if a room has a user as member
     * @param room room, in which the user might be
     * @param user user, which is to be checked
     * @return if user is member of room
     */
    boolean roomHasAsMember(Room room, RegisteredUser user);


    /**
     * Adds a user to the activeMembers in a {@link VoiceChannel} in a room.
     * @param voiceChannelID id of the voiceChannel, in which the user should be added.
     * @param room room, in which the VoiceChannel is
     * @param user user, which should be added to the voiceChannel-members
     * @throws InvalidChannelException if the Channel is not in room
     * @throws InvalidUserException if the user is no member of the room
     * @throws ChannelDoesNotExistException if the channelID does not match a voiceChannel in the database
     * @throws InvalidCallStateException if problems occur when the user must first leave a call to enter voiceChannel (does likely no happen)
     */
    void joinVoiceChannelInRoom(long voiceChannelID, Room room, RegisteredUser user) throws InvalidChannelException, InvalidUserException, ChannelDoesNotExistException, InvalidCallStateException;
}
