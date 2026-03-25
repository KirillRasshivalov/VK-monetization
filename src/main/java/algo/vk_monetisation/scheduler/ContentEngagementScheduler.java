package algo.vk_monetisation.scheduler;

import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.Content;
import algo.vk_monetisation.repositories.AdvertisingCampaignRepository;
import algo.vk_monetisation.repositories.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentEngagementScheduler {

    private final AdvertisingCampaignRepository advertisingCampaignRepository;
    private final ContentRepository contentRepository;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void tick() {
        LocalDateTime now = LocalDateTime.now();
        List<AdvertisingCampaign> activeCampaigns =
                advertisingCampaignRepository.findByStatus(AdvertisingCampaign.CampaignStatus.ACTIVE);
        if (activeCampaigns.isEmpty()) {
            return;
        }
        int incremented = 0;
        int completed = 0;
        List<Content> contentsToSave = new ArrayList<>();
        for (AdvertisingCampaign campaign : activeCampaigns) {
            Content content = campaign.getContent();
            if (content == null) {
                continue;
            }
            if (campaign.getEndDate() != null && !campaign.getEndDate().isAfter(now)) {
                if (content.getFinalViews() == null) {
                    content.setFinalViews(content.getViews());
                    content.setFinalLikes(content.getLikes());
                    content.setStatsFixedAt(now);
                }
                campaign.setStatus(AdvertisingCampaign.CampaignStatus.COMPLETED);
                completed++;
                contentsToSave.add(content);
                continue;
            }
            long views = content.getViews() == null ? 0L : content.getViews();
            long likes = content.getLikes() == null ? 0L : content.getLikes();
            views += 1;
            if (views % 5 == 0) {
                likes += 1;
            }
            content.setViews(views);
            content.setLikes(likes);
            incremented++;
            contentsToSave.add(content);
        }
        advertisingCampaignRepository.saveAll(activeCampaigns);
        contentRepository.saveAll(contentsToSave);
        log.debug("Engagement tick: incremented={}, completed={}", incremented, completed);
    }
}

