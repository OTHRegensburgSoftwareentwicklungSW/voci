package de.majaf.voci.control.service;

import de.majaf.voci.entity.RegisteredUser;

public interface IDropsiService {

    RegisteredUser updateDropsiToken(RegisteredUser user, String token);
}
