package de.majaf.voci.entity.repo;

import de.majaf.voci.entity.RegisteredUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RegisteredUserRepository extends CrudRepository<RegisteredUser, Long> {
    Optional<RegisteredUser> findByUserName(String userName);
    Optional<RegisteredUser> findByEmail(String email);
    Optional<RegisteredUser> findBySecurityToken(String securityToken);
}
