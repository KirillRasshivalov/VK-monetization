package algo.vk_monetisation.repositories;

import algo.vk_monetisation.entities.RegistrationInnLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationInnLedgerRepository extends JpaRepository<RegistrationInnLedger, Long> {
    Optional<RegistrationInnLedger> findByInn(String inn);
    List<RegistrationInnLedger> findByPersonIdOrderByCreatedAtAsc(Long personId);
}
