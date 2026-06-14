package algo.vk_monetisation.kafka;

import algo.vk_monetisation.jca.FnsConnection;
import algo.vk_monetisation.jca.FnsConnectionFactory;
import algo.vk_monetisation.services.InnResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InnCheckListeners {

    private final FnsConnectionFactory fnsConnectionFactory;
    private final InnResultService innResultService;
    @Value("${app.cluster.node-id:single-node}")
    private String nodeId;

    @KafkaListener(
            topics = "inn_check_primary",
            groupId = "primary-group",
            autoStartup = "${app.cluster.registration-inn-primary-listener-enabled:true}"
    )
    public void onPrimary(String inn) {
        handle(inn, "primary");
    }

    @KafkaListener(
            topics = "inn_check_secondary",
            groupId = "secondary-group",
            autoStartup = "${app.cluster.registration-inn-secondary-listener-enabled:true}"
    )
    public void onSecondary(String inn) {
        handle(inn, "secondary");
    }

    private void handle(String inn, String node) {
        log.info("Listener {} received INN={}", node, inn);

        // вызов JCA вне JTA
        String result;
        try (FnsConnection conn = fnsConnectionFactory.getConnection()) {
            result = conn.verifyInn(inn);
        } catch (Exception e) {
            log.error("Error while calling JCA for INN {}: {}", inn, e.getMessage());
            result = "ERROR";
        }

        // делегируем применение результата в отдельную JTA транзакцию
        innResultService.applyResult(inn, node + "@" + nodeId, result);
    }
}
