package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.service.exceptions.channel.ChannelIDDoesNotExistException;
import de.majaf.voci.entity.Message;
import de.majaf.voci.entity.TextChannel;
import de.majaf.voci.entity.User;
import de.majaf.voci.entity.repo.MessageRepository;
import de.majaf.voci.entity.repo.TextChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ChannelService implements IChannelService {

    @Autowired
    private TextChannelRepository textChannelRepo;

    @Autowired
    private MessageRepository messageRepo;

    @Override
    @Transactional
    public TextChannel loadTextChannelByID(long textChannelID) throws ChannelIDDoesNotExistException {
        return textChannelRepo.findById(textChannelID).orElseThrow(() -> new ChannelIDDoesNotExistException(textChannelID, "Text Channel does not exist"));
    }

    @Override
    @Transactional
    public TextChannel createTextChannel() {
        TextChannel textChannel = new TextChannel();
        textChannelRepo.save(textChannel);
        return textChannel;
    }

    @Override
    @Transactional
    public void deleteTextChannel(TextChannel channel) {
        textChannelRepo.delete(channel);
    }

    @Override
    @Transactional
    public void deleteTextChannelByID(long textChannelID) throws ChannelIDDoesNotExistException {
        TextChannel textChannel = textChannelRepo.findById(textChannelID).orElseThrow(() -> new ChannelIDDoesNotExistException(textChannelID, "Text Channel does not exist"));
        deleteTextChannel(textChannel);
    }

    @Override
    @Transactional
    public Message createTextMessage(String msg, long textChannelID, User sender) throws ChannelIDDoesNotExistException {
        TextChannel textChannel = loadTextChannelByID(textChannelID);
        Message message = new Message(msg, sender);
        textChannel.addMessage(message);
        textChannelRepo.save(textChannel);
        return message;
    }

}
