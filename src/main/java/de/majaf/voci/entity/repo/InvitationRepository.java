package de.majaf.voci.entity.repo;

import de.majaf.voci.entity.Invitation;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InvitationRepository extends CrudRepository<Invitation, Long> {
    Optional<Invitation> findByAccessToken(String accessToken);
}
