package de.majaf.voci.control.service;

import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.Room;

import java.util.List;


public interface IRoomService {
    void createRoom(String roomName, RegisteredUser owner);
    void deleteRoom(long ID);
}
