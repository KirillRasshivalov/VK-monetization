package algo.vk_monetisation.repositories;

import algo.vk_monetisation.entities.AdvertisingCampaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisingCampaignRepository extends JpaRepository<AdvertisingCampaign, Long> {
    boolean existsByCampaignId(Long campaignId);
}
