package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.exceptions.InvalidNameException;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.exceptions.channel.InvalidChannelException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.service.IRoomService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.exceptions.room.RoomDoesNotExistException;
import de.majaf.voci.entity.*;
import de.majaf.voci.entity.repo.RoomRepository;
import de.majaf.voci.entity.repo.VoiceChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Scope(value = "singleton")
public class RoomService implements IRoomService {

    @Autowired
    private IUserService userService;

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    @Qualifier("voiceChannelService")
    private IChannelService voiceChannelService;

    @Autowired
    private ICallService callService;

    @Override
    @Transactional
    public Room loadRoomByID(long id) throws RoomDoesNotExistException {
        return roomRepo.findById(id).orElseThrow(() -> new RoomDoesNotExistException(id, "Invalid Room-ID"));
    }

    @Override
    @Transactional
    public void createRoom(String roomName, RegisteredUser owner) throws InvalidNameException {
        if(roomName == null || (roomName.trim().equals("")) || roomName.trim().length()>20) throw new InvalidNameException(roomName, "Invalid room-name");
        TextChannel textChannel = new TextChannel("general");
        VoiceChannel voiceChannel = new VoiceChannel("general");
        Room room = new Room(roomName.trim(), owner, textChannel, voiceChannel);
        owner.addOwnedRoom(room);
        owner.addRoom(room);
        room.setOwner(owner);
        room.addMember(owner);
        roomRepo.save(room);
    }

    @Override
    public Room saveRoom(Room room) {
        return roomRepo.save(room);
    }

    @Override
    public boolean roomHasAsMember(Room room, RegisteredUser user) {
        return room.getMembers().contains(user);
    }

    @Override
    @Transactional
    public void addMemberToRoom(Room room, long memberID, RegisteredUser initiator) throws InvalidUserException, UserDoesNotExistException {
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
    public void removeMemberFromRoom(Room room, long memberID, RegisteredUser initiator) throws InvalidUserException, UserDoesNotExistException {
        if (room.getOwner().equals(initiator)) {
            RegisteredUser member = (RegisteredUser) userService.loadUserByID(memberID);
            member.removeRoom(room);
            room.removeMember(member);
            if (member.getActiveVoiceChannel() != null)
                for (VoiceChannel vc : room.getVoiceChannels())
                    if (vc.equals(member.getActiveVoiceChannel())) {
                        userService.leaveVoiceChannel(member);
                        break;
                    }

            roomRepo.save(room);
        } else throw new InvalidUserException(initiator, "Initiator is not owner of this room.");
    }

    @Override
    @Transactional
    public void deleteRoom(long roomID, RegisteredUser initiator) throws RoomDoesNotExistException, InvalidUserException {
        Room room = loadRoomByID(roomID);
        if (initiator.equals(room.getOwner())) {
            for (User member : room.getMembers())
                if (member.getActiveVoiceChannel() != null)
                    for (VoiceChannel vc : room.getVoiceChannels())
                        if (vc.equals(member.getActiveVoiceChannel())) {
                            userService.leaveVoiceChannel(member);
                            break;
                        }
            roomRepo.delete(room);
        } else throw new InvalidUserException(initiator, "User is not owner of this room.");
    }

    @Override
    @Transactional
    public void leaveRoom(long roomID, RegisteredUser member) throws RoomDoesNotExistException, InvalidUserException {
        Room room = loadRoomByID(roomID);
        if (!member.equals(room.getOwner())) {
            if (member.getActiveVoiceChannel() != null)
                for (VoiceChannel vc : room.getVoiceChannels())
                    if (vc.equals(member.getActiveVoiceChannel())) {
                        userService.leaveVoiceChannel(member);
                        break;
                    }
            member.removeRoom(room);
            room.removeMember(member);
            roomRepo.save(room);
        } else throw new InvalidUserException(member, "User cannot leave. He is owner.");
    }

    @Override
    @Transactional
    public void joinVoiceChannelInRoom(long voiceChannelID, Room room, RegisteredUser user) throws InvalidChannelException, InvalidUserException, ChannelDoesNotExistException, InvalidCallStateException {
        if (roomHasAsMember(room, user)) {
            if (user.getActiveCall() != null) {
                callService.leaveCall(user);
            }

            if (user.getActiveVoiceChannel() != null) {
                userService.leaveVoiceChannel(user);
            }

            VoiceChannel voiceChannel = (VoiceChannel) voiceChannelService.loadChannelByIDInRoom(voiceChannelID, room);
            voiceChannel.addActiveMember(user);
            user.setActiveVoiceChannel(voiceChannel);
            userService.saveUser(user);
        } else throw new InvalidUserException(user, "Invitor is no member of this room.");
    }
}
