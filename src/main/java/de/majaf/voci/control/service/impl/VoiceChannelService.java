package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.exceptions.InvalidNameException;
import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.exceptions.channel.InvalidChannelException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.service.IRoomService;
import de.majaf.voci.entity.*;
import de.majaf.voci.entity.repo.VoiceChannelRepository;
import de.mschoettle.entity.dto.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Component("voiceChannelService")
public class VoiceChannelService implements IChannelService {

    @Autowired
    private VoiceChannelRepository voiceChannelRepo;

    @Autowired
    private IRoomService roomService;

    @Override
    @Transactional
    public VoiceChannel createChannel() {
        VoiceChannel voiceChannel = new VoiceChannel();
        voiceChannelRepo.save(voiceChannel);
        return voiceChannel;
    }

    @Override
    public VoiceChannel loadChannelByID(long channelID) throws ChannelDoesNotExistException {
        return voiceChannelRepo.findById(channelID).orElseThrow(() -> new ChannelDoesNotExistException(channelID, "Voice-Channel does not exist"));
    }

    @Override
    @Transactional
    public VoiceChannel loadChannelByIDInRoom(long channelID, Room room) throws ChannelDoesNotExistException, InvalidChannelException {
        VoiceChannel channel = loadChannelByID(channelID);
        if (room.getVoiceChannels().contains(channel)) {
            return channel;
        } else throw new InvalidChannelException(channel, "Channel is not in room.");
    }

    @Override
    @Transactional
    public void deleteChannel(Channel channel) {
        voiceChannelRepo.delete((VoiceChannel) channel);
    }

    @Override
    @Transactional
    public void deleteChannelByID(long channelID) throws ChannelDoesNotExistException {
        VoiceChannel voiceChannel = voiceChannelRepo.findById(channelID).orElseThrow(() -> new ChannelDoesNotExistException(channelID, "Voice-Channel does not exist"));
        deleteChannel(voiceChannel);
    }

    @Override
    @Transactional
    public void addChannelToRoom(Room room, String channelName, RegisteredUser initiator) throws InvalidUserException, InvalidNameException {
        if (initiator.equals(room.getOwner())) {
            if (channelName != null && !channelName.equals("")) {
                VoiceChannel voiceChannel = new VoiceChannel(channelName);
                room.addVoiceChannel(voiceChannel);
                roomService.saveRoom(room);
            } else throw new InvalidNameException(channelName, "Channel-Name is empty");
        } else throw new InvalidUserException(initiator, "User is not Owner");
    }

    @Override
    @Transactional
    public void deleteChannelFromRoom(Room room, long channelID, RegisteredUser initiator) throws InvalidUserException, ChannelDoesNotExistException {
        if (initiator.equals(room.getOwner())) {
            if (room.getVoiceChannels().size() > 1) {
                VoiceChannel voiceChannel = (VoiceChannel) loadChannelByID(channelID);
                room.removeVoiceChannel(voiceChannel);
                deleteChannel(voiceChannel);
            } // TODO: else Exception schmeißen
        } else throw new InvalidUserException(initiator, "User is not Owner");
    }

    @Override
    @Transactional
    public void renameChannel(long channelID, Room room, String channelName, RegisteredUser initiator) throws InvalidNameException, InvalidUserException, ChannelDoesNotExistException, InvalidChannelException {
        if (initiator.equals(room.getOwner())) {
            VoiceChannel channel = loadChannelByID(channelID);
            if (room.getVoiceChannels().contains(channel)) {
                if (channelName != null && !channelName.equals("")) {
                    channel.setChannelName(channelName);
                    voiceChannelRepo.save(channel);
                } else throw new InvalidNameException(channelName, "Channel-Name is empty");
            } else throw new InvalidChannelException(channel, "Channel is not in room.");
        } else throw new InvalidUserException(initiator, "User is not Owner");
    }

    @Override
    @Transactional
    public Message createTextMessage(String msg, long channelID, User user) throws ChannelDoesNotExistException {
        return null;
    }

    @Override
    public Message createDropsiFileMessage(FileDTO file, long textChannelID, User sender) throws ChannelDoesNotExistException {
        return null;
    }
}
