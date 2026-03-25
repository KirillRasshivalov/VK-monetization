package algo.vk_monetisation.managers;

import algo.vk_monetisation.dto.WalletTopUpDTO;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceHandler {

    private final PersonRepository personRepository;

    @Transactional
    public void topUp(WalletTopUpDTO dto) {
        Person person = personRepository.findById(dto.personId()).get();
        Double balance = person.getBalance();
        if (balance == null) {
            balance = 0.0;
        }
        person.setBalance(balance + dto.amount());
        personRepository.save(person);
        log.info("Баланс обновлен для personId={}, balance={}", person.getId(), person.getBalance());
    }
}
