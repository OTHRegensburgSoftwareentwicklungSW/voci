package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.IRoomService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.Room;
import de.majaf.voci.entity.User;
import de.majaf.voci.entity.repo.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService implements IRoomService {

    @Autowired
    IUserService userService;

    @Autowired
    RoomRepository roomRepo;

    @Override
    public void createRoom(String roomName, RegisteredUser owner) {
        Room room = new Room(roomName, owner);
        owner.addOwnedRoom(room);
        owner.addRoom(room);
        room.setOwner(owner);
        room.addMember(owner);
        roomRepo.save(room);
    }

    @Override
    public void deleteRoom(long ID) {

    }

}
