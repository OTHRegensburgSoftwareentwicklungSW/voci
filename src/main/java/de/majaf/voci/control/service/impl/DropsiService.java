package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.IDropsiService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
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

}
