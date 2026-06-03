package algo.vk_monetisation.services;

import algo.vk_monetisation.jca.FnsConnectionFactory;
import algo.vk_monetisation.jca.FnsConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class InnVerificationService {

    private final FnsConnectionFactory fnsConnectionFactory;

    public boolean verifyInn(String inn) {
        if (inn == null || inn.isEmpty()) {
            log.warn("INN неверный формат");
            return false;
        }
        try {
            log.info("Проверка INN: {} через ФНС систему", maskInn(inn));
            FnsConnection connection = fnsConnectionFactory.getConnection();
            try {
                String verificationResult = connection.verifyInn(inn);
                boolean isValid = "VERIFIED".equals(verificationResult);
                if (isValid) {
                    log.info("INN {} verification: SUCCESS", maskInn(inn));
                } else {
                    log.warn("INN {} verification: FAILED - {}", maskInn(inn), verificationResult);
                }
                return isValid;
            } finally {
                connection.close();
            }
        } catch (Exception e) {
            log.error("Ошибка во время верификации ИНН: {}", maskInn(inn), e);
            return false;
        }
    }

    private String maskInn(String inn) {
        if (inn == null || inn.length() < 4) {
            return "****";
        }
        return inn.substring(0, 2) + "****" + inn.substring(inn.length() - 2);
    }
}

