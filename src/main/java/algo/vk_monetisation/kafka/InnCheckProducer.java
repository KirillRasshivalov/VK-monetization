package algo.vk_monetisation.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InnCheckProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendToPrimary(String key, String payload) {
        log.info("Sending INN check event to primary topic: key={}", key);
        kafkaTemplate.send("inn_check_primary", key, payload);
    }

    public void sendToSecondary(String key, String payload) {
        log.info("Sending INN check event to secondary topic: key={}", key);
        kafkaTemplate.send("inn_check_secondary", key, payload);
    }
}
