package de.majaf.voci.entity.repo;

import de.majaf.voci.entity.RegisteredUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<RegisteredUser, Integer> {

    Optional<RegisteredUser> findByUserName(String userName);
    Optional<RegisteredUser> findByEmail(String email);
}
