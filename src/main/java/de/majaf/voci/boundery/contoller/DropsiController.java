package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.call.DropsiException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.IDropsiService;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import de.mschoettle.entity.dto.FileSystemObjectDTO;
import de.mschoettle.entity.dto.FolderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.net.UnknownHostException;

//TODO: make exceptionHandling with unknownHostException
@Controller @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DropsiController {

    @Autowired
    private IDropsiService dropsiService;

    @Autowired
    ControllerUtils controllerUtils;

    @Autowired
    RestTemplate restService;

    private final String dropsiURL = "http://im-codd:8922/";

    @RequestMapping(value = "/info/updateDropsi", method = RequestMethod.POST)
    public String changeDropsiToken(@ModelAttribute("dropsiToken") String dropsiToken, Authentication auth, Model model) throws UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        if (dropsiToken != null && !dropsiToken.equals("")) {
            try {
                getRootFolder(dropsiToken);
                FileSystemObjectDTO rootFolder = restService.getForObject(dropsiURL + "api/rootfolder?secretKey=" + dropsiToken, FileSystemObjectDTO.class);

                if (rootFolder != null)
                    user = dropsiService.updateDropsiToken(user, dropsiToken);

            } catch(DropsiException e) {
                model.addAttribute("errorMsg", "No connection or no valid Dropsi-Security-Token");
            }
        }
        model.addAttribute("user", user);
        return "info";
    }

    public FolderDTO getRootFolder(String dropsiToken) throws DropsiException {
        FolderDTO rootFolder;
        try {
            rootFolder = restService.getForObject(dropsiURL + "api/rootfolder?secretKey=" + dropsiToken, FolderDTO.class);
        } catch (Exception e) {
            throw new DropsiException("No connection or no valid Dropsi-Token!", e);
        }
        return rootFolder;
    }

    public byte[] getFileFromID(long fileID, String dropsiToken) throws DropsiException {
        try {
            return restService.getForObject(dropsiURL + "api/files/" + fileID + "?secretKey="+ dropsiToken, byte[].class);
        } catch (Exception e) {
            throw new DropsiException("Problem connecting to Dropsi!", e);
        }
    }

    public void addDropsiFilesToModel(Model model, RegisteredUser user) {
        if (user.getDropsiToken() != null) {
            FolderDTO rootFolder;
            try {
                rootFolder = getRootFolder(user.getDropsiToken());
                model.addAttribute("fileList", dropsiService.getFilesFromRootFolder(rootFolder));
            } catch (DropsiException e) {
                model.addAttribute("dropsiErrorMsg", "Problems connecting to Dropsi. If this problem continues, please update your Dropsi-Token.");
            }
        }
    }
}
