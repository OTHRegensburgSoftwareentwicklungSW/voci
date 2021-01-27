package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.entity.DropsiFileMessage;
import de.majaf.voci.entity.Message;
import de.majaf.voci.entity.User;
import de.mschoettle.entity.dto.FileDTO;

/**
 * Service interface for managing Messages
 */
public interface IMessageService {

    /**
     * Creates a {@link Message}-object and saves it in the database
     *
     * @param msg message-content
     * @param channelID id of channel, in which the message is sent
     * @param sender sender of the message
     * @return Message, which contains additional information on the message-content
     * @throws ChannelDoesNotExistException if the channel, in which the messsage is sent, does not exist.
     */
    Message createTextMessage(String msg, long channelID, User sender) throws ChannelDoesNotExistException;

    /**
     * Filters Dropsi-{@link FileDTO} data and creates a {@link DropsiFileMessage}-object and saves it in the database
     *
     * @param file object containing information on the file at Drospi
     * @param textChannelID id of channel, in which the message is sent
     * @param sender sender of the message
     * @return Message, which contains additional information on the file
     * @throws ChannelDoesNotExistException if the channel, in which the message is sent, does not exist.
     */
    Message createDropsiFileMessage(FileDTO file, long textChannelID, User sender) throws ChannelDoesNotExistException;

    /**
     * Appends file-binary data, which maybe is retrieved from Dropsi to a {@link DropsiFileMessage}-object. The binary data is not saved in the database.
     * @param msg message to which binary data is appended
     * @param data binary data
     * @return message with appended binary-data
     */
    DropsiFileMessage updateBinary(DropsiFileMessage msg, byte[] data);

}
