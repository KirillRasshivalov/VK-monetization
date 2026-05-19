package algo.vk_monetisation.eis;

import algo.vk_monetisation.entities.EisIntegrationLog;
import algo.vk_monetisation.eis.jca.dadata.DadataConnection;
import algo.vk_monetisation.eis.jca.dadata.DadataConnectionFactory;
import algo.vk_monetisation.eis.jca.dadata.DadataInnRequest;
import algo.vk_monetisation.eis.jca.dadata.DadataInnResponse;
import algo.vk_monetisation.eis.jca.smsru.SmsRuConnection;
import algo.vk_monetisation.eis.jca.smsru.SmsRuConnectionFactory;
import algo.vk_monetisation.eis.jca.smsru.SmsRuSendRequest;
import algo.vk_monetisation.eis.jca.smsru.SmsRuSendResponse;
import algo.vk_monetisation.messaging.CampaignLifecycleEvent;
import algo.vk_monetisation.repositories.EisIntegrationLogRepository;
import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EisProcessingService {

    private final DadataConnectionFactory dadataConnectionFactory;
    private final SmsRuConnectionFactory smsRuConnectionFactory;
    private final EisIntegrationLogRepository eisIntegrationLogRepository;

    @Value("${app.cluster.node-id}")
    private String nodeId;

    @Transactional
    public void validateInnCompliance(CampaignLifecycleEvent event) {
        if (event.inn() == null || event.inn().isBlank()) {
            saveLog("DADATA", "VALIDATE_INN", event.campaignId(),
                    "inn=null", "INN отсутствует", "SKIPPED");
            return;
        }
        DadataInnRequest request = new DadataInnRequest(event.inn());
        try (DadataConnection connection = dadataConnectionFactory.getConnection()) {
            DadataInnResponse response = connection.validateInn(request);
            String status = response.valid() ? "SUCCESS" : "FAILED";
            saveLog("DADATA", "VALIDATE_INN", event.campaignId(),
                    request.inn(), response.rawBody(), status);
            log.info("[{}] DaData INN {} для кампании {}: valid={}",
                    nodeId, event.inn(), event.campaignId(), response.valid());
        } catch (ResourceException e) {
            saveLog("DADATA", "VALIDATE_INN", event.campaignId(),
                    request.inn(), e.getMessage(), "ERROR");
            throw new IllegalStateException("Ошибка JCA DaData: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void sendCampaignNotification(CampaignLifecycleEvent event) {
        String phone = event.contactPhone();
        if (phone == null || phone.isBlank()) {
            saveLog("SMSRU", "SEND_SMS", event.campaignId(),
                    "phone=null", "Телефон отсутствует", "SKIPPED");
            return;
        }
        String message = buildSmsText(event);
        SmsRuSendRequest request = new SmsRuSendRequest(normalizePhone(phone), message);
        try (SmsRuConnection connection = smsRuConnectionFactory.getConnection()) {
            SmsRuSendResponse response = connection.sendSms(request);
            String status = response.success() ? "SUCCESS" : "FAILED";
            saveLog("SMSRU", "SEND_SMS", event.campaignId(),
                    request.phone() + " | " + message, response.rawBody(), status);
            log.info("[{}] SMS.ru кампания {}: success={}",
                    nodeId, event.campaignId(), response.success());
        } catch (ResourceException e) {
            saveLog("SMSRU", "SEND_SMS", event.campaignId(),
                    request.phone(), e.getMessage(), "ERROR");
            throw new IllegalStateException("Ошибка JCA SMS.ru: " + e.getMessage(), e);
        }
    }

    private String buildSmsText(CampaignLifecycleEvent event) {
        if ("COMPLETED".equals(event.eventType())) {
            return "VK Monetisation: кампания #" + event.campaignId()
                    + " завершена. Итог: просмотры=" + event.views() + ", лайки=" + event.likes();
        }
        return "VK Monetisation: кампания #" + event.campaignId()
                + " активирована. Рекламодатель: " + nullSafe(event.companyName());
    }

    private String normalizePhone(String phone) {
        return phone.replaceAll("[^0-9+]", "");
    }

    private String nullSafe(String value) {
        return value != null ? value : "—";
    }

    private void saveLog(String provider, String operation, Long campaignId,
                         String request, String response, String status) {
        EisIntegrationLog logEntry = new EisIntegrationLog();
        logEntry.setProvider(provider);
        logEntry.setOperation(operation);
        logEntry.setCampaignId(campaignId);
        logEntry.setRequestPayload(request);
        logEntry.setResponsePayload(response != null && response.length() > 4000
                ? response.substring(0, 4000) : response);
        logEntry.setStatus(status);
        logEntry.setProcessedByNode(nodeId);
        eisIntegrationLogRepository.save(logEntry);
    }
}
