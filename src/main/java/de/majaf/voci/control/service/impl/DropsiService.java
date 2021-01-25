package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.IDropsiService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.DropsiFileMessage;
import de.majaf.voci.entity.RegisteredUser;
import de.mschoettle.entity.dto.FileDTO;
import de.mschoettle.entity.dto.FileSystemObjectDTO;
import de.mschoettle.entity.dto.FolderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service @Scope("singleton")
public class DropsiService implements IDropsiService {

    @Autowired
    private IUserService userService;

    @Override
    public RegisteredUser updateDropsiToken(RegisteredUser user, String token) {
        if (token == null || token.equals(""))
            user.setDropsiToken(null);
        else user.setDropsiToken(token);
        userService.saveUser(user);
        return user;
    }

    @Override
    public List<FileDTO> getFilesFromRootFolder(FolderDTO rootFolder) {
        return getFilesFromFolder(rootFolder, "/root");
    }

    @Override
    public DropsiFileMessage updateBinary(DropsiFileMessage msg, byte[] data) {
        msg.setPayload(data);
        return msg;
    }

    private List<FileDTO> getFilesFromFolder(FolderDTO folder, String path) {
        List<FileDTO> files = new ArrayList<>();
        if (folder != null) {
            for (FileSystemObjectDTO child : folder.getContents()) {
                if (child instanceof FileDTO) {
                    String pathName = path + "/" + child.getName();
                    String fileExtension = ((FileDTO) child).getFileExtension();
                    if (fileExtension != null)
                        pathName = pathName + "." + fileExtension;

                    child.setName(pathName);
                    files.add((FileDTO) child);
                } else {
                    files.addAll(getFilesFromFolder((FolderDTO) child, path + "/" + child.getName()));
                }
            }
        }
        return files;
    }

}
