package de.majaf.voci.boundery.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@Configuration
public class SecurityUtilities {

    private static final int hashStrength = 15;

    private static final String salt = "RANDMORSTH";

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(hashStrength, new SecureRandom(salt.getBytes()));
    }
}