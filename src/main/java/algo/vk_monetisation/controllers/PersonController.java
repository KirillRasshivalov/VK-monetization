package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.PersonDTO;
import algo.vk_monetisation.dto.PersonResponseDTO;
import algo.vk_monetisation.services.PersonSevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/persons")
public class PersonController {

    private final PersonSevice personSevice;

    @PostMapping("/create_person")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('MODERATOR', 'USER')")
    public void createPerson(@RequestBody PersonDTO personDTO) {
        log.info("Пришел запрос на добавление нового ответсвтенного лица компании.");
        personSevice.createPerson(personDTO);
    }

    @GetMapping("/get_person/{personId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('MODERATOR', 'USER')")
    public PersonResponseDTO getPerson(@PathVariable Long personId) {
        log.info("Пришел зарос на показ ответственного лица.");
        return personSevice.getPerson(personId);
    }

    @PutMapping("/update_person/{personId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MODERATOR')")
    public void updatePerson(@PathVariable Long personId, @RequestBody PersonDTO personDTO) {
        log.info("Пришел запрос на обновление ответственного лица компании.");
        personSevice.updatePerson(personId, personDTO);
    }

    @DeleteMapping("/delete_person/{personId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MODERATOR')")
    public void deletePerson(@PathVariable Long personId) {
        log.info("Пришел зарос на удалние ответственного лица.");
        personSevice.deletePerson(personId);
    }
}
