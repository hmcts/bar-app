package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.BarUser;

import java.util.Optional;

@Repository
public interface BarUserRepository extends BaseRepository<BarUser, String> {

    Optional<BarUser> findBarUserById(String id);
}
