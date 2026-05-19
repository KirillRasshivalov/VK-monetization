package algo.vk_monetisation.scheduler;

import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.Content;
import algo.vk_monetisation.messaging.CampaignAsyncMessagingService;
import algo.vk_monetisation.repositories.AdvertisingCampaignRepository;
import algo.vk_monetisation.repositories.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.scheduler.engagement.enabled", havingValue = "true", matchIfMissing = true)
public class ContentEngagementScheduler {

    private final AdvertisingCampaignRepository advertisingCampaignRepository;
    private final ContentRepository contentRepository;
    private final CampaignAsyncMessagingService asyncMessagingService;

    @Value("${app.scheduler.engagement.fixed-rate-ms:60000}")
    private long fixedRateMs;

    @Scheduled(fixedRateString = "${app.scheduler.engagement.fixed-rate-ms:60000}")
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void tick() {
        LocalDateTime now = LocalDateTime.now();
        List<AdvertisingCampaign> activeCampaigns = advertisingCampaignRepository.findByStatusWithContent(
                AdvertisingCampaign.CampaignStatus.ACTIVE);
        if (activeCampaigns.isEmpty()) {
            return;
        }
        int incremented = 0;
        int completed = 0;
        List<Content> contentsToSave = new ArrayList<>();
        List<CompletionNotification> completionNotifications = new ArrayList<>();

        for (AdvertisingCampaign campaign : activeCampaigns) {
            List<Content> contents = campaign.getContent();
            if (contents == null || contents.isEmpty()) {
                continue;
            }
            boolean campaignExpired = campaign.getEndDate() != null && !campaign.getEndDate().isAfter(now);
            if (campaignExpired) {
                for (Content content : contents) {
                    if (content.getFinalViews() == null) {
                        content.setFinalViews(content.getViews());
                        content.setFinalLikes(content.getLikes());
                        content.setStatsFixedAt(now);
                        contentsToSave.add(content);
                        completionNotifications.add(new CompletionNotification(
                                campaign.getId(),
                                content.getId(),
                                campaign.getPerson().getId()
                        ));
                    }
                }
                campaign.setStatus(AdvertisingCampaign.CampaignStatus.COMPLETED);
                completed++;
                continue;
            }
            for (Content content : contents) {
                long views = content.getViews() == null ? 0L : content.getViews();
                long likes = content.getLikes() == null ? 0L : content.getLikes();
                views += 1;
                if (views % 5 == 0) {
                    likes += 1;
                }
                content.setViews(views);
                content.setLikes(likes);
                contentsToSave.add(content);
                incremented++;
            }
        }

        advertisingCampaignRepository.saveAll(activeCampaigns);
        if (!contentsToSave.isEmpty()) {
            contentRepository.saveAll(contentsToSave);
        }

        if (!completionNotifications.isEmpty()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    for (CompletionNotification n : completionNotifications) {
                        asyncMessagingService.publishCompleted(n.campaignId(), n.contentId(), n.personId());
                    }
                }
            });
        }

        log.debug("Engagement tick: incremented={}, completed={}, rateMs={}", incremented, completed, fixedRateMs);
    }

    private record CompletionNotification(Long campaignId, Long contentId, Long personId) {
    }
}
