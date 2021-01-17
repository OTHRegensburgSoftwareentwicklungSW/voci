package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.room.RoomIDDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.Room;


public interface IRoomService {
    Room loadRoomByID(long id) throws RoomIDDoesNotExistException;
    void createRoom(String roomName, RegisteredUser owner);
    Room saveRoom(Room room);
    boolean roomHasAsMember(Room room, RegisteredUser user);
    void addMemberToRoom(Room room, long memberID, RegisteredUser initiator) throws InvalidUserException, UserDoesNotExistException;
    void removeMemberFromRoom(Room room, long memberID, RegisteredUser initiator) throws InvalidUserException, UserDoesNotExistException;
    void deleteRoom(long roomID, RegisteredUser initiator) throws RoomIDDoesNotExistException, InvalidUserException;
    void leaveRoom(long roomID, RegisteredUser member) throws RoomIDDoesNotExistException, InvalidUserException;
}
