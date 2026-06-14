package algo.vk_monetisation.managers;

import algo.vk_monetisation.dto.CompanyInfoDTO;
import algo.vk_monetisation.dto.RequisitesDTO;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.entities.RegistrationInnLedger;
import algo.vk_monetisation.kafka.InnCheckProducer;
import algo.vk_monetisation.repositories.PersonRepository;
import algo.vk_monetisation.repositories.RegistrationInnLedgerRepository;
import algo.vk_monetisation.utils.RequisitesMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonHandler {

    private final PersonRepository personRepository;
    private final RegistrationInnLedgerRepository ledgerRepository;
    private final InnCheckProducer innCheckProducer;
    private final RequisitesMapper requisitesMapper;

    @Transactional(isolation = Isolation.REPEATABLE_READ,
            rollbackFor = Exception.class)
    public void addMainPerson(RequisitesDTO requisitesDTO) {
        log.info("Добавляем лицо отвечающее за компанию.");
        CompanyInfoDTO companyInfoDTO = requisitesDTO.companyInfoDTO();
        Person person = requisitesMapper.toPersonEntity(requisitesDTO);
        if (person.getLegalEntity() != null && person.getLegalEntity().getInn() == null) {
            person.getLegalEntity().setInn(companyInfoDTO.inn());
        }
        log.debug("Сущность Person и связанные данные собраны.");

        personRepository.save(person);

        // Создаем запись в ledger в той же JTA транзакции
        RegistrationInnLedger ledger = new RegistrationInnLedger();
        ledger.setInn(companyInfoDTO.inn());
        ledger.setPersonId(person.getId());
        ledgerRepository.save(ledger);

        // Регистрируем синхронизацию, чтобы отправить сообщение только после коммита транзакции
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // После успешного коммита отправляем события в две очереди
                    String inn = companyInfoDTO.inn();
                    log.info("Transaction committed — отправляем события проверки ИНН для {}", inn);
                    innCheckProducer.sendToPrimary(ledger.getId().toString(), inn);
                    innCheckProducer.sendToSecondary(ledger.getId().toString(), inn);
                }
            });
        } else {
            log.warn("TransactionSynchronization не активен — отправка событий выполнится синхронно");
            innCheckProducer.sendToPrimary(ledger.getId().toString(), companyInfoDTO.inn());
            innCheckProducer.sendToSecondary(ledger.getId().toString(), companyInfoDTO.inn());
        }
    }

}
