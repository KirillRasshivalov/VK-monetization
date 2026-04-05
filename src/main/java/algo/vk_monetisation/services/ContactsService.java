package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.ContactsDTO;
import algo.vk_monetisation.entities.Contacts;
import algo.vk_monetisation.repositories.ContactsRepository;
import algo.vk_monetisation.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContactsService {

    private final Validator validator;

    private final ContactsRepository contactsRepository;

    private final ModelMapper modelMapper;


    public Contacts getContacts(Long contactsId) {
        log.info("Пришел запрос на сервис на получение контактов.");
        validator.validateContacts(contactsId);
        return contactsRepository.findById(contactsId);
    }

    public void createContacts(ContactsDTO contacts) {
        log.info("Пришел запрос на сервис для создания контактов.");
        validator.validateContacts(contacts);
        Contacts newContacts = new Contacts();
        newContacts.setContactPerson(contacts.contactPearson());
        newContacts.setContactNumber(contacts.contactNumber());
        contactsRepository.save(newContacts);
        log.info("Контакт усмешно составлен.");
    }

    public void updateContacts(Long contactsId, Contacts contacts) {
        log.info("Пришел запрос на сервис на обновление контактов лица.");
        validator.validateContacts(contactsId, contacts);
        Contacts prevContacts = contactsRepository.findById(contactsId);
        modelMapper.map(contacts, prevContacts);
        contactsRepository.save(prevContacts);
        log.info("Обновление контактов прошло успешно.");
    }
}
