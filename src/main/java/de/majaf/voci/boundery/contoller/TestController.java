package de.majaf.voci.boundery.contoller;

import de.majaf.voci.entity.Invitation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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

@Controller @Scope("singleton")
public class TestController {

    @Autowired
    private RestTemplate restServiceClient;

    private final String url = "http://im-codd:8945/api/";
    @RequestMapping(value = "/test/start")
    public String test() {
        String key = "5f1498db-2f40-4824-a156-1a0e57719ff8";
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
        String key = "5f1498db-2f40-4824-a156-1a0e57719ff8";
        String accessToken = "8a919694-ab60-4128-95fa-57a74817bef5";

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
