package algo.vk_monetisation.repositories;

import algo.vk_monetisation.entities.EisIntegrationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EisIntegrationLogRepository extends JpaRepository<EisIntegrationLog, Long> {
    List<EisIntegrationLog> findByPersonIdOrderByCreatedAtAsc(Long personId);
    List<EisIntegrationLog> findByInnOrderByCreatedAtAsc(String inn);
}
