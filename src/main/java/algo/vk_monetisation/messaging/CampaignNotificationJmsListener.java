package algo.vk_monetisation.messaging;

import algo.vk_monetisation.eis.EisProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Узел 2: SMS-уведомления рекламодателю через SMS.ru (JCA).
 */
@Component
@ConditionalOnProperty(name = "app.cluster.notification-listener.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class CampaignNotificationJmsListener {

    private final EisProcessingService eisProcessingService;

    @Value("${app.cluster.node-id}")
    private String nodeId;

    @JmsListener(destination = "${app.messaging.queue.notifications}", containerFactory = "jmsListenerContainerFactory")
    public void onNotificationMessage(CampaignLifecycleEvent event) {
        log.info("[{}] JMS notification: type={}, campaignId={}", nodeId, event.eventType(), event.campaignId());
        eisProcessingService.sendCampaignNotification(event);
    }
}
