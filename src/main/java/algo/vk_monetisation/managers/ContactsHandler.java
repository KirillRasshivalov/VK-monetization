package algo.vk_monetisation.managers;

import algo.vk_monetisation.dto.ContactsDTO;
import algo.vk_monetisation.entities.Contacts;
import algo.vk_monetisation.repositories.ContactsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContactsHandler {

    private final ContactsRepository contactsRepository;

    private final ModelMapper modelMapper;

    public Contacts showContacts(Long contactId) {
        log.info("Начало сбора информации о контактах.");
        return contactsRepository.findById(contactId);
    }

    @Transactional
    public void createContact(ContactsDTO contacts) {
        log.info("Начало добавления контактов.");
        Contacts newContacts = new Contacts();
        newContacts.setContactPerson(contacts.contactPearson());
        newContacts.setContactNumber(contacts.contactNumber());
        contactsRepository.save(newContacts);
        log.info("Контакт усмешно составлен.");
    }

    @Transactional
    public void updateContact(Long contactsId, Contacts contacts) {
        log.info("Начало обновления контактов.");
        Contacts prevContacts = contactsRepository.findById(contactsId);
        modelMapper.map(contacts, prevContacts);
        contactsRepository.save(prevContacts);
        log.info("Обновление контактов прошло успешно.");
    }
}
