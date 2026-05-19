package algo.vk_monetisation.messaging;

import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.Content;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.repositories.AdvertisingCampaignRepository;
import algo.vk_monetisation.repositories.ContentRepository;
import algo.vk_monetisation.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignAsyncMessagingService {

    private final AdvertisingCampaignRepository campaignRepository;
    private final ContentRepository contentRepository;
    private final PersonRepository personRepository;
    private final StompCampaignEventPublisher stompPublisher;

    @Transactional(readOnly = true)
    public void publishActivated(Long campaignId, Long contentId, Long personId) {
        AdvertisingCampaign campaign = campaignRepository.findById(campaignId).orElse(null);
        Content content = contentRepository.findById(contentId).orElse(null);
        Person person = personRepository.findWithDetailsById(personId).orElse(null);
        if (campaign == null || content == null || person == null) {
            log.warn("Не удалось собрать событие ACTIVATED: campaign={}, content={}, person={}",
                    campaignId, contentId, personId);
            return;
        }
        CampaignLifecycleEvent event = CampaignLifecycleEventFactory.activated(campaign, content, person);
        stompPublisher.publishAfterActivation(event);
    }

    @Transactional(readOnly = true)
    public void publishCompleted(Long campaignId, Long contentId, Long personId) {
        AdvertisingCampaign campaign = campaignRepository.findById(campaignId).orElse(null);
        Content content = contentRepository.findById(contentId).orElse(null);
        Person person = personRepository.findWithDetailsById(personId).orElse(null);
        if (campaign == null || content == null || person == null) {
            log.warn("Не удалось собрать событие COMPLETED: campaign={}, content={}, person={}",
                    campaignId, contentId, personId);
            return;
        }
        CampaignLifecycleEvent event = CampaignLifecycleEventFactory.completed(campaign, content, person);
        stompPublisher.publishAfterCompletion(event);
    }
}
