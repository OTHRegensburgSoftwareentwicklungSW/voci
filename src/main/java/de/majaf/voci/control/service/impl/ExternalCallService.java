package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.IExternalCallService;
import org.springframework.stereotype.Service;

@Service
public class ExternalCallService implements IExternalCallService {

    @Override
    public String generateCallURL(String userToken) {
        return null;
    }

    @Override
    public void joinCall(String userToken, String callURL) {

    }
}
