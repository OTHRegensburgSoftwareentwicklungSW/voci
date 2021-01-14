package de.majaf.voci.entity.repo;

import de.majaf.voci.entity.GuestUser;
import de.majaf.voci.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
