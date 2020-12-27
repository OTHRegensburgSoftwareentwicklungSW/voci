package de.majaf.voci.control.service;

import org.springframework.stereotype.Service;


public interface IExternalCallService {
    String generateCallURL(String userToken);
    void joinCall(String userToken, String callURL);
}
