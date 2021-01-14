package de.majaf.voci.entity.repo;

import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.Invitation;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CallRepository extends CrudRepository<Call, Long> {
}
