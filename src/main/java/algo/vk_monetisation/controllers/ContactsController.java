package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.ContactsDTO;
import algo.vk_monetisation.dto.ContactsResponseDTO;
import algo.vk_monetisation.entities.Contacts;
import algo.vk_monetisation.services.ContactsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/contacts")
public class ContactsController {

    private final ContactsService contactsService;

    @GetMapping("/show_contacts/{contactsId}")
    @ResponseStatus(HttpStatus.OK)
    public ContactsResponseDTO showContacts(@PathVariable Long contactsId) {
        log.info("Пришел запрос на показ контактов.");
        return contactsService.getContacts(contactsId);
    }

    @PostMapping("/create_contacts/{personId}")
    @ResponseStatus(HttpStatus.OK)
    public void createContacts(@PathVariable Long personId,@RequestBody ContactsDTO contactsDTO) {
        log.info("Пришел запрос на добавление контактов.");
        contactsService.createContacts(personId, contactsDTO);
    }

    @PutMapping("/update_contacts/{contactsId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateContacts(@PathVariable Long contactsId, @RequestBody ContactsDTO contactsDTO) {
        log.info("Пришел запрос на обновление контактов представителя.");
        contactsService.updateContacts(contactsId, contactsDTO);
    }

//    @DeleteMapping("/delete_contacts/{contactsId}")
//    @ResponseStatus(HttpStatus.OK)
//    public void deleteContacts(@PathVariable Long contactsId) {
//        log.info("Пришел запрос на удаление сконтактов.");
//
//    }

}
