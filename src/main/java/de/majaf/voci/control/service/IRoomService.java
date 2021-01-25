package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.exceptions.room.RoomDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.Room;
import de.majaf.voci.entity.TextChannel;

import javax.transaction.Transactional;


public interface IRoomService {
    Room saveRoom(Room room);
    Room loadRoomByID(long id) throws RoomDoesNotExistException;

    void createRoom(String roomName, RegisteredUser owner);

    void addMemberToRoom(Room room, long memberID, RegisteredUser initiator) throws InvalidUserException, UserDoesNotExistException;
    void removeMemberFromRoom(Room room, long memberID, RegisteredUser initiator) throws InvalidUserException, UserDoesNotExistException;

    void leaveRoom(long roomID, RegisteredUser member) throws RoomDoesNotExistException, InvalidUserException;
    void deleteRoom(long roomID, RegisteredUser initiator) throws RoomDoesNotExistException, InvalidUserException;

    boolean roomHasAsMember(Room room, RegisteredUser user);

}
