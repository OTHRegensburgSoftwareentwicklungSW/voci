package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserIDDoesNotExistException;
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
    @Transactional
    public void addMemberToRoom(Room room, long memberID, RegisteredUser initiator) throws InvalidUserException, UserIDDoesNotExistException {
        if (roomHasAsMember(room, initiator)) {
            RegisteredUser member = (RegisteredUser) userService.loadUserByID(memberID);
            if (initiator.getContacts().contains(member)) {
                member.addRoom(room);
                room.addMember(member);
                roomRepo.save(room);
            } else throw new InvalidUserException(member, "Invited user is not in contacts");
        } else throw new InvalidUserException(initiator, "Invitor is no member of this room.");
    }

    @Override
    @Transactional
    public void removeMemberFromRoom(Room room, long memberID, RegisteredUser initiator) throws InvalidUserException, UserIDDoesNotExistException {
        if (roomHasAsMember(room, initiator)) {
            RegisteredUser member = (RegisteredUser) userService.loadUserByID(memberID);
            member.removeRoom(room);
            room.removeMember(member);
            roomRepo.save(room);
        } else throw new InvalidUserException(initiator, "Initiator is no member of this room.");
    }

    @Override
    @Transactional
    public void deleteRoom(long roomID, RegisteredUser initiator) throws RoomIDDoesNotExistException, InvalidUserException {
        Room room = loadRoomByID(roomID);
        if (initiator.equals(room.getOwner())) {
            roomRepo.delete(room);
        } else throw new InvalidUserException(initiator, "User is not owner of this room.");
    }

    @Override
    @Transactional
    public void leaveRoom(long roomID, RegisteredUser member) throws RoomIDDoesNotExistException, InvalidUserException {
        Room room = loadRoomByID(roomID);
        if (!member.equals(room.getOwner())) {
            member.removeRoom(room);
            room.removeMember(member);
            roomRepo.save(room);
        } else throw new InvalidUserException(member, "User cannot leave. He is owner.");
    }

}
