package algo.vk_monetisation.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Отправка событий в RabbitMQ через протокол STOMP (требование лаб. 3).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StompCampaignEventPublisher {

    @Value("${app.messaging.stomp.broker-url}")
    private String brokerUrl;

    @Value("${app.messaging.stomp.login}")
    private String login;

    @Value("${app.messaging.stomp.passcode}")
    private String passcode;

    @Value("${app.messaging.queue.compliance}")
    private String complianceQueue;

    @Value("${app.messaging.queue.notifications}")
    private String notificationsQueue;

    public void publishAfterActivation(CampaignLifecycleEvent event) {
        sendToQueue(complianceQueue, event);
        sendToQueue(notificationsQueue, event);
    }

    public void publishAfterCompletion(CampaignLifecycleEvent event) {
        sendToQueue(notificationsQueue, event);
    }

    private void sendToQueue(String queueName, CampaignLifecycleEvent event) {
        String destination = MessagingQueueNames.stompQueueDestination(queueName);
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.setLogin(login);
        connectHeaders.setPasscode(passcode);

        try {
            StompSession session = stompClient
                    .connectAsync(brokerUrl, new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter())
                    .get(15, TimeUnit.SECONDS);

            session.send(destination, event);
            log.info("STOMP: событие {} отправлено в {} (campaignId={})",
                    event.eventType(), destination, event.campaignId());
            session.disconnect();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("STOMP отправка прервана для очереди {}: {}", queueName, e.getMessage());
        } catch (ExecutionException | TimeoutException e) {
            log.warn("STOMP недоступен ({}), событие campaignId={} не доставлено в {}: {}",
                    brokerUrl, event.campaignId(), queueName, e.getMessage());
        }
    }

    private static class StompSessionHandlerAdapter extends org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return CampaignLifecycleEvent.class;
        }
    }
}
