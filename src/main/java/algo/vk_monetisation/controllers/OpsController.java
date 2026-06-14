package algo.vk_monetisation.controllers;

import algo.vk_monetisation.entities.EisIntegrationLog;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.entities.RegistrationInnLedger;
import algo.vk_monetisation.repositories.EisIntegrationLogRepository;
import algo.vk_monetisation.repositories.PersonRepository;
import algo.vk_monetisation.repositories.RegistrationInnLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ops")
@RequiredArgsConstructor
public class OpsController {

    private final EisIntegrationLogRepository eisIntegrationLogRepository;
    private final RegistrationInnLedgerRepository registrationInnLedgerRepository;
    private final PersonRepository personRepository;

    @GetMapping("/eis-logs/person/{personId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public List<EisIntegrationLog> getEisLogsByPerson(@PathVariable Long personId) {
        return eisIntegrationLogRepository.findByPersonIdOrderByCreatedAtAsc(personId);
    }

    @GetMapping("/eis-logs/inn/{inn}")
    @PreAuthorize("hasRole('MODERATOR')")
    public List<EisIntegrationLog> getEisLogsByInn(@PathVariable String inn) {
        return eisIntegrationLogRepository.findByInnOrderByCreatedAtAsc(inn);
    }

    @GetMapping("/inn-ledger/person/{personId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public List<RegistrationInnLedger> getInnLedgerByPerson(@PathVariable Long personId) {
        return registrationInnLedgerRepository.findByPersonIdOrderByCreatedAtAsc(personId);
    }

    @GetMapping("/persons/email/{email}")
    @PreAuthorize("hasRole('MODERATOR')")
    public Long getPersonIdByEmail(@PathVariable String email) {
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Person не найден по email: " + email));
        return person.getId();
    }
}
