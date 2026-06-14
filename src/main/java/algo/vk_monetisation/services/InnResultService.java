package algo.vk_monetisation.services;

import algo.vk_monetisation.entities.EisIntegrationLog;
import algo.vk_monetisation.entities.RegistrationInnLedger;
import algo.vk_monetisation.repositories.CompanyInfoRepository;
import algo.vk_monetisation.repositories.EisIntegrationLogRepository;
import algo.vk_monetisation.repositories.RegistrationInnLedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InnResultService {

    private final RegistrationInnLedgerRepository ledgerRepository;
    private final CompanyInfoRepository companyInfoRepository;
    private final EisIntegrationLogRepository eisLogRepository;

    @Transactional
    public void applyResult(String inn, String node, String result) {
        RegistrationInnLedger ledger = ledgerRepository.findByInn(inn).orElse(null);
        boolean verified = "VERIFIED".equals(result);

        if (ledger != null) {
            if (node != null && node.startsWith("primary")) ledger.setPrimaryChecked(verified);
            else ledger.setSecondaryChecked(verified);
            ledgerRepository.save(ledger);
        }

        companyInfoRepository.findByInn(inn).ifPresent(ci -> {
            if (node != null && node.startsWith("primary")) ci.setInnPrimaryDadataOk(verified);
            else ci.setInnSecondaryDadataOk(verified);
            companyInfoRepository.save(ci);
        });

        EisIntegrationLog logEntry = new EisIntegrationLog();
        logEntry.setInn(inn);
        logEntry.setIntegrationPoint(node);
        logEntry.setPersonId(ledger != null ? ledger.getPersonId() : null);
        logEntry.setResult(result);
        eisLogRepository.save(logEntry);
        log.info("Applied INN result. inn={}, node={}, result={}", inn, node, result);
    }
}
