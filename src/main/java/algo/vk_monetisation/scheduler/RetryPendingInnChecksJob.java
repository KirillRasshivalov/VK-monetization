package algo.vk_monetisation.scheduler;

import algo.vk_monetisation.entities.RegistrationInnLedger;
import algo.vk_monetisation.kafka.InnCheckProducer;
import algo.vk_monetisation.repositories.RegistrationInnLedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RetryPendingInnChecksJob implements Job {

    private final RegistrationInnLedgerRepository ledgerRepository;
    private final InnCheckProducer innCheckProducer;

    @Override
    public void execute(JobExecutionContext context) {
        List<RegistrationInnLedger> pending = ledgerRepository.findAll();
        for (RegistrationInnLedger r : pending) {
            if (Boolean.FALSE.equals(r.getPrimaryChecked())) {
                log.info("Retrying primary INN check for {}", r.getInn());
                innCheckProducer.sendToPrimary(r.getId().toString(), r.getInn());
            }
            if (Boolean.FALSE.equals(r.getSecondaryChecked())) {
                log.info("Retrying secondary INN check for {}", r.getInn());
                innCheckProducer.sendToSecondary(r.getId().toString(), r.getInn());
            }
        }
    }
}
