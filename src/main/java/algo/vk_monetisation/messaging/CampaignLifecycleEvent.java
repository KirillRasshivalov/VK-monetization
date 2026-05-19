package algo.vk_monetisation.messaging;

import java.io.Serializable;

/**
 * Событие жизненного цикла кампании для асинхронной обработки (STOMP → JMS).
 */
public record CampaignLifecycleEvent(
        String eventType,
        Long campaignId,
        Long personId,
        Long contentId,
        String inn,
        String companyName,
        String contactPhone,
        String contactPerson,
        Long views,
        Long likes,
        String occurredAt
) implements Serializable {
}
