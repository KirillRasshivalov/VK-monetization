package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.ContactsDTO;
import algo.vk_monetisation.entities.Contacts;
import algo.vk_monetisation.managers.ContactsHandler;
import algo.vk_monetisation.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContactsService {

    private final ContactsHandler contactsHandler;

    private final Validator validator;

    public Contacts getContacts(Long contactsId) {
        log.info("Пришел запрос на сервис на получение контактов.");
        validator.validateContacts(contactsId);
        return contactsHandler.showContacts(contactsId);
    }

    public void createContacts(ContactsDTO contacts) {
        log.info("Пришел запрос на сервис для создания контактов.");
        validator.validateContacts(contacts);
        contactsHandler.createContact(contacts);
    }

    public void updateContacts(Long contactsId, Contacts contacts) {
        log.info("Пришел запрос на сервис на обновление контактов лица.");
        validator.validateContacts(contactsId, contacts);
        contactsHandler.updateContact(contactsId, contacts);
    }
}
