package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.call.DropsiException;
import de.majaf.voci.control.service.IDropsiService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.RegisteredUser;
import de.mschoettle.entity.dto.FileSystemObjectDTO;
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
            try {
                FileSystemObjectDTO rootFolder = restService.getForObject(dropsiURL + "api/rootfolder?secretKey=" + dropsiToken, FileSystemObjectDTO.class);
                if (rootFolder != null)
                    user = dropsiService.updateDropsiToken(user, dropsiToken);
            } catch (Exception e) {
                throw new DropsiException("No valid Dropsi-Token!");
            }
        }
        model.addAttribute("user", user);
        return "info";
    }
}
