package algo.vk_monetisation.repositories;

import algo.vk_monetisation.entities.CompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Long> {
    Optional<CompanyInfo> findByInn(String inn);
}
