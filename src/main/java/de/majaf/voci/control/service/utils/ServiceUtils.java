package de.majaf.voci.control.service.utils;

import org.springframework.stereotype.Component;

/**
 * Utils used by multiple Services
 */
@Component
public class ServiceUtils {

    /**
     * checks if a name is empty or longer than 20 chars
     * @param name name, which is to be checked
     * @return if name is valid
     */
    public boolean checkName(String name) {
        return name != null && !(name.trim()).equals("") && (name.trim()).length() <= 40;
    }
}
