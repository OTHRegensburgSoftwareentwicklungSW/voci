package de.majaf.voci.control.service.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class LoggerFactory {

    @Bean
    public Logger getLogger() {
        return Logger.getLogger("VOCI");
    }

}
