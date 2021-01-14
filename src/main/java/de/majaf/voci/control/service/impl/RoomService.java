package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.IRoomService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.exceptions.room.RoomIDDoesNotExistException;
import de.majaf.voci.entity.*;
import de.majaf.voci.entity.repo.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RoomService implements IRoomService {

    @Autowired
    IUserService userService;

    @Autowired
    RoomRepository roomRepo;

    @Override
    @Transactional
    public Room loadRoomByID(long id) throws RoomIDDoesNotExistException {
        return roomRepo.findById(id).orElseThrow(() -> new RoomIDDoesNotExistException(id, "Invalid Room-ID"));
    }

    @Override
    @Transactional
    public void createRoom(String roomName, RegisteredUser owner) {
        TextChannel textChannel = new TextChannel("general");
        VoiceChannel voiceChannel = new VoiceChannel("general");
        Room room = new Room(roomName, owner, textChannel, voiceChannel);
        owner.addOwnedRoom(room);
        owner.addRoom(room);
        room.setOwner(owner);
        room.addMember(owner);
        roomRepo.save(room);
    }

    @Override
    public boolean roomHasAsMember(Room room, RegisteredUser user) {
        return room.getMembers().contains(user);
    }

    @Override
    public void deleteRoom(long ID) {

    }

}
