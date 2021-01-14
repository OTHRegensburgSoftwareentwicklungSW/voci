package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.room.RoomIDDoesNotExistException;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.Room;


public interface IRoomService {
    Room loadRoomByID(long id) throws RoomIDDoesNotExistException;
    void createRoom(String roomName, RegisteredUser owner);
    boolean roomHasAsMember(Room room, RegisteredUser user);
    void deleteRoom(long id);
}
