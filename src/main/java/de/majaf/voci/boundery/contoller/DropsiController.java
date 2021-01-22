package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.call.DropsiException;
import de.majaf.voci.control.service.IDropsiService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.RegisteredUser;
import de.mschoettle.entity.dto.FileDTO;
import de.mschoettle.entity.dto.FileSystemObjectDTO;
import de.mschoettle.entity.dto.FolderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;

@Controller
public class DropsiController {

    @Autowired
    private MainController mainController;

    @Autowired
    private IDropsiService dropsiService;

    @Autowired
    RestTemplate restService;

    private final String dropsiURL = "http://im-codd:8922/";

    @RequestMapping(value = "/info/updateDropsi", method = RequestMethod.POST)
    public String changeDropsiToken(@ModelAttribute("dropsiToken") String dropsiToken, Principal principal, Model model) throws DropsiException {
        RegisteredUser user = mainController.getActiveUser(principal);
        if (dropsiToken != null && !dropsiToken.equals("")) {
            getRootFolder(dropsiToken);
            FileSystemObjectDTO rootFolder = restService.getForObject(dropsiURL + "api/rootfolder?secretKey=" + dropsiToken, FileSystemObjectDTO.class);
            if (rootFolder != null)
                user = dropsiService.updateDropsiToken(user, dropsiToken);
        }
        model.addAttribute("user", user);
        return "info";
    }

    public FolderDTO getRootFolder(String dropsiToken) throws DropsiException {
        FolderDTO rootFolder;
        try {
            rootFolder = restService.getForObject(dropsiURL + "api/rootfolder?secretKey=" + dropsiToken, FolderDTO.class);
        } catch (Exception e) {
            throw new DropsiException("No valid Dropsi-Token!", e);
        }
        return rootFolder;
    }

    public byte[] getFileFromID(long fileID, String dropsiToken) throws DropsiException {
        try {
            byte[] file = restService.getForObject(dropsiURL + "api/files/" + fileID + "?secretKey="+ dropsiToken, byte[].class);
            return file;
        } catch (Exception e) {
            throw new DropsiException("Problem connecting to Dropsi!", e);
        }
    }

    public void addDropsiFilesToModel(Model model, RegisteredUser user) throws DropsiException {
        if (user.getDropsiToken() != null) {
            FolderDTO rootFolder = getRootFolder(user.getDropsiToken());
            model.addAttribute("fileList", dropsiService.getFilesFromRootFolder(rootFolder));
        }
    }
}
