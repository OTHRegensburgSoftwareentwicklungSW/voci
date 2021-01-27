package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.call.DropsiException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.IDropsiService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.RegisteredUser;
import de.mschoettle.entity.dto.FolderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@Scope("session")
public class InfoController {

    @Autowired
    private ControllerUtils controllerUtils;

    @Autowired
    private DropsiController dropsiController;

    @Autowired
    private IUserService userService;

    private Date sessionStart = new Date();

    DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String prepareInfoPage(Authentication auth, Model model) throws UserDoesNotExistException {
        addInfoAttributesToModel(model, (RegisteredUser) controllerUtils.getActiveUser(auth));
        return "info";
    }

    @RequestMapping(value = "/info/updateDropsi", method = RequestMethod.POST)
    public String changeDropsiToken(@ModelAttribute("dropsiToken") String dropsiToken, Authentication auth, Model model) throws UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        if (dropsiToken != null && !dropsiToken.equals("")) {
            try {
                FolderDTO rootFolder = dropsiController.getRootFolder(dropsiToken);

                if (rootFolder != null)
                    user = userService.updateDropsiToken(user, dropsiToken);

            } catch(DropsiException e) {
                model.addAttribute("errorMsg", "No connection or no valid Dropsi-Security-Token");
            }
        } else user = userService.updateDropsiToken(user, null);
        addInfoAttributesToModel(model, user);
        return "info";
    }

    private void addInfoAttributesToModel(Model model, RegisteredUser user) {
        model.addAttribute("user", user);
        model.addAttribute("sessionStart", df.format(sessionStart));
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public void handleUserDoesNotExistException(HttpServletResponse response, UserDoesNotExistException e) throws IOException {
        controllerUtils.handleUserDoesNotExistException(response, e);
    }
}
