package de.majaf.voci.control.service.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UsernameDoesNotExistException extends UsernameNotFoundException {
    private String username;

    public UsernameDoesNotExistException(String message) {
        super(message);
    }

    public UsernameDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameDoesNotExistException(String username, String message) {
        this(message);
        this.username = username;
    }

    public UsernameDoesNotExistException(String username, String message, Throwable cause) {
        this(message, cause);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
