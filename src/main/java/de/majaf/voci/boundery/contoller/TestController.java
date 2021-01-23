package de.majaf.voci.boundery.contoller;

import de.majaf.voci.entity.Invitation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TestController {

    @Autowired
    private RestTemplate restServiceClient;

    @Autowired
    private MainController mainController;

    private final String url = "http://im-codd:8945/api/";
    @RequestMapping(value = "/test/start")
    public String test() {
        String key = "be387e28-8ff2-4d62-8b25-ce6eb86364d7";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("securityToken", key);
        HttpEntity<String> request = new HttpEntity<>(headers);
        System.out.println(request);
        Invitation invitation = restServiceClient.postForObject(url + "startCall", request, Invitation.class);
        System.out.println(invitation);
        return "error";
    }

    @RequestMapping(value = "/test/end")
    public String test2() {
        String key = "be387e28-8ff2-4d62-8b25-ce6eb86364d7";
        String accessToken = "066dfd73-1e1e-49bd-9155-eb1d6847c467";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("securityToken", key);
        HttpEntity<String> request = new HttpEntity<>(headers);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url + "endCall").
                queryParam("accessToken", accessToken);

        ResponseEntity<String> response = restServiceClient.exchange(uriBuilder.toUriString(), HttpMethod.DELETE, request, String.class);
        return "error";
    }
}
