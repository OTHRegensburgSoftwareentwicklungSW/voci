package de.majaf.voci.entity.repo;

import de.majaf.voci.entity.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {
}
