package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.ContactsDTO;
import algo.vk_monetisation.dto.ContactsResponseDTO;
import algo.vk_monetisation.entities.Contacts;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.exceptions.ValidationException;
import algo.vk_monetisation.repositories.ContactsRepository;
import algo.vk_monetisation.repositories.PersonRepository;
import algo.vk_monetisation.utils.ContactsMapper;
import algo.vk_monetisation.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContactsService {

    private final Validator validator;

    private final ContactsRepository contactsRepository;

    private final ContactsMapper contactsMapper;

    private final PersonRepository personRepository;


    public ContactsResponseDTO getContacts(Long contactsId) {
        log.info("Пришел запрос на сервис на получение контактов.");
        validator.validateContacts(contactsId);
        Contacts contacts = contactsRepository.findById(contactsId);
        return contactsMapper.contactsToContactsDTO(contacts);
    }

    public void createContacts(Long personId, ContactsDTO contacts) {
        log.info("Пришел запрос на сервис для создания контактов.");
        validator.validateContacts(contacts);
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ValidationException("Person не найден: " + personId));
        Contacts newContacts = new Contacts();
        newContacts.setContactPerson(contacts.contactPerson());
        newContacts.setContactNumber(contacts.contactNumber());
        newContacts.setPerson(person);
        contactsRepository.save(newContacts);
        log.info("Контакт усмешно составлен.");
    }

    public void updateContacts(Long contactsId, ContactsDTO contactsDTO) {
        log.info("Пришел запрос на сервис на обновление контактов лица.");
        validator.validateContacts(contactsId, contactsDTO);
        Contacts prevContacts = contactsRepository.findById(contactsId);
        contactsMapper.updateContacts(prevContacts, contactsDTO);
        contactsRepository.save(prevContacts);
        log.info("Обновление контактов прошло успешно.");
    }
}
