package algo.vk_monetisation.messaging;

import algo.vk_monetisation.eis.EisProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Узел 1: проверка рекламодателя через DaData (JCA) после активации кампании.
 */
@Component
@ConditionalOnProperty(name = "app.cluster.compliance-listener.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class CampaignComplianceJmsListener {

    private final EisProcessingService eisProcessingService;

    @Value("${app.cluster.node-id}")
    private String nodeId;

    @JmsListener(destination = "${app.messaging.queue.compliance}", containerFactory = "jmsListenerContainerFactory")
    public void onComplianceMessage(CampaignLifecycleEvent event) {
        if (!"ACTIVATED".equals(event.eventType())) {
            return;
        }
        log.info("[{}] JMS compliance: campaignId={}, inn={}", nodeId, event.campaignId(), event.inn());
        eisProcessingService.validateInnCompliance(event);
    }
}
