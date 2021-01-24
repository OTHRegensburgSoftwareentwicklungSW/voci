package de.majaf.voci.boundery.contoller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller @Scope("singleton")
public class SwaggerController {

    @RequestMapping(value ="/swagger-ui")
    public String showSwagger() {
        return "redirect:/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#";
    }
}
