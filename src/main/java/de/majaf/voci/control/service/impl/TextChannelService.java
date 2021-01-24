package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.exceptions.InvalidNameException;
import de.majaf.voci.control.exceptions.channel.InvalidChannelException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.service.IRoomService;
import de.majaf.voci.entity.*;
import de.majaf.voci.entity.repo.MessageRepository;
import de.majaf.voci.entity.repo.TextChannelRepository;
import de.mschoettle.entity.dto.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Scope(value = "singleton")
@Component("textChannelService")
public class TextChannelService implements IChannelService {

    @Autowired
    private TextChannelRepository textChannelRepo;

    @Autowired
    private IRoomService roomService;

    @Override
    @Transactional
    public TextChannel loadChannelByID(long textChannelID) throws ChannelDoesNotExistException {
        return textChannelRepo.findById(textChannelID).orElseThrow(() -> new ChannelDoesNotExistException(textChannelID, "Text Channel does not exist"));
    }

    @Override
    public TextChannel loadChannelByIDInRoom(long channelID, Room room) throws ChannelDoesNotExistException, InvalidChannelException {
        TextChannel channel = loadChannelByID(channelID);
        if (room.getTextChannels().contains(channel)) {
            return channel;
        } else throw new InvalidChannelException(channel, "Channel is not in room.");
    }

    @Override
    @Transactional
    public TextChannel createChannel() {
        TextChannel textChannel = new TextChannel();
        textChannelRepo.save(textChannel);
        return textChannel;
    }

    @Override
    @Transactional
    public void deleteChannel(Channel channel) {
        textChannelRepo.delete((TextChannel) channel);
    }

    @Override
    @Transactional
    public void addChannelToRoom(Room room, String channelName, RegisteredUser initiator) throws InvalidUserException, InvalidNameException {
        if (initiator.equals(room.getOwner())) {
            if (channelName != null && !channelName.equals("")) {
                TextChannel textChannel = new TextChannel(channelName);
                room.addTextChannel(textChannel);
                roomService.saveRoom(room);
            } else throw new InvalidNameException(channelName, "Channel-Name is empty");
        } else throw new InvalidUserException(initiator, "User is not Owner");
    }

    @Override
    @Transactional
    public void deleteChannelFromRoom(Room room, long channelID, RegisteredUser initiator) throws InvalidUserException, ChannelDoesNotExistException {
        if (initiator.equals(room.getOwner())) {
            if (room.getTextChannels().size() > 1) {
                TextChannel textChannel = loadChannelByID(channelID);
                room.removeTextChannel(textChannel);
                deleteChannel(textChannel);
            } // TODO: else Exception schmeißen
        } else throw new InvalidUserException(initiator, "User is not Owner");
    }

    @Override
    @Transactional
    public void renameChannel(long channelID, Room room, String channelName, RegisteredUser initiator) throws InvalidNameException, InvalidUserException, ChannelDoesNotExistException, InvalidChannelException {
        if (initiator.equals(room.getOwner())) {
            TextChannel channel = loadChannelByID(channelID);
            if (room.getTextChannels().contains(channel)) {
                if (channelName != null && !channelName.equals("")) {
                    channel.setChannelName(channelName);
                    textChannelRepo.save(channel);
                } else throw new InvalidNameException(channelName, "Channel-Name is empty");
            } else throw new InvalidChannelException(channel, "Channel is not in room.");
        } else throw new InvalidUserException(initiator, "User is not Owner");
    }

    @Override
    public boolean userIsInChannel(long channelID, User user) {
        Call call = user.getActiveCall();
        if (call != null) {
            if (call.getTextChannel().getId() == channelID)
                return true;
        }
        if (user instanceof RegisteredUser)
            for (Room room : ((RegisteredUser) user).getRooms()) {
                for (TextChannel textChannel : room.getTextChannels())
                    if (textChannel.getId() == channelID)
                        return true;
            }
        return false;
    }

    @Override
    @Transactional
    public Message createTextMessage(String msg, long textChannelID, User sender) throws ChannelDoesNotExistException {
        TextChannel textChannel = loadChannelByID(textChannelID);
        Message message = new TextMessage(msg, sender);
        textChannel.addMessage(message);
        textChannelRepo.save(textChannel);
        return message;
    }

    @Override
    @Transactional
    public Message createDropsiFileMessage(FileDTO file, long textChannelID, User sender) throws ChannelDoesNotExistException {
        TextChannel textChannel = loadChannelByID(textChannelID);
        Message message = new DropsiFileMessage(file, sender);
        textChannel.addMessage(message);
        textChannelRepo.save(textChannel);
        return message;
    }

}
