package de.majaf.voci.control.service;

import de.majaf.voci.entity.DropsiFileMessage;
import de.majaf.voci.entity.RegisteredUser;
import de.mschoettle.entity.dto.FileDTO;
import de.mschoettle.entity.dto.FolderDTO;

import java.util.List;

public interface IDropsiService {

    RegisteredUser updateDropsiToken(RegisteredUser user, String token);
    List<FileDTO> getFilesFromRootFolder(FolderDTO rootFolder);
    DropsiFileMessage updateBinary(DropsiFileMessage msg, byte[] data);
}
