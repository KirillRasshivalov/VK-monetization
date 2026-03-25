package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.WalletTopUpDTO;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.exceptions.ValidationException;
import algo.vk_monetisation.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final PersonRepository personRepository;

    @Transactional
    public void topUp(WalletTopUpDTO dto) {
        if (dto == null || dto.personId() == null) {
            throw new ValidationException("personId не должен быть пустым");
        }
        if (dto.amount() == null || dto.amount() <= 0) {
            throw new ValidationException("amount должен быть > 0");
        }

        Person person = personRepository.findById(dto.personId())
                .orElseThrow(() -> new ValidationException("Person не найден: " + dto.personId()));

        Double balance = person.getBalance();
        if (balance == null) {
            balance = 0.0;
        }
        person.setBalance(balance + dto.amount());

        personRepository.save(person);
        log.info("Баланс обновлен для personId={}, balance={}", person.getId(), person.getBalance());
    }
}

