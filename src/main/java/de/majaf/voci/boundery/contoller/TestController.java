package de.majaf.voci.boundery.contoller;

import de.majaf.voci.entity.Invitation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TestController {

    @Autowired
    private RestTemplate restServiceClient;

    @Autowired
    private MainController mainController;

    private final String url = "http://voci:8945/api/";
    @RequestMapping(value = "test")
    public String test(Model model, Principal principal) {
        String key = "6efd0eb5-204f-4831-9dde-0d702817fcf0";
        Invitation invitation = restServiceClient.getForObject(url + "startCall?securityToken=" + key, Invitation.class);
        System.out.println(invitation);
        return "error";
    }
}
