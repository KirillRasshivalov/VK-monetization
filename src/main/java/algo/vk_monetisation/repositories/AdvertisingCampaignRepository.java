package algo.vk_monetisation.repositories;

import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.Content;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdvertisingCampaignRepository extends JpaRepository<AdvertisingCampaign, Long> {

    List<AdvertisingCampaign> findByStatus(AdvertisingCampaign.CampaignStatus status);


    @Query("SELECT DISTINCT ct FROM Content ct " +
            "LEFT JOIN FETCH ct.advertisingCampaign " +
            "WHERE ct.advertisingCampaign.id = :campaignId")
    List<Content> findContentsByCampaignId(@Param("campaignId") Long campaignId, Pageable pageable);

}
