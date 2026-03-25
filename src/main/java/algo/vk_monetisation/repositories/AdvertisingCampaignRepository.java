package algo.vk_monetisation.repositories;

import algo.vk_monetisation.entities.AdvertisingCampaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertisingCampaignRepository extends JpaRepository<AdvertisingCampaign, Long> {

//    boolean existsByCampaignId(Long campaignId);

    List<AdvertisingCampaign> findByStatus(AdvertisingCampaign.CampaignStatus status);
}
