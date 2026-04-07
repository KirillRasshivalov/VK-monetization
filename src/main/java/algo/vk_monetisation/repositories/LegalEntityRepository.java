package algo.vk_monetisation.repositories;

import algo.vk_monetisation.entities.LegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LegalEntityRepository extends JpaRepository<LegalEntity, Long> {
    Optional<LegalEntity> findById(Long id);
}
