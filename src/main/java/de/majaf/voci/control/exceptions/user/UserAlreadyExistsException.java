package de.majaf.voci.control.exceptions.user;

import de.majaf.voci.entity.User;

public class UserAlreadyExistsException extends InvalidUserException{
    public UserAlreadyExistsException() {
        super();
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public UserAlreadyExistsException(User user) {
        super(user);
    }

    public UserAlreadyExistsException(User user, String message) {
        super(user, message);
    }

    public UserAlreadyExistsException(User user, String message, Throwable cause) {
        super(user, message, cause);
    }

    public UserAlreadyExistsException(User user, Throwable cause) {
        super(user, cause);
    }
}
