package algo.vk_monetisation.managers;

import algo.vk_monetisation.dto.CompanyInfoDTO;
import algo.vk_monetisation.dto.ContactsDTO;
import algo.vk_monetisation.dto.LegalEntityDTO;
import algo.vk_monetisation.dto.RequisitesDTO;
import algo.vk_monetisation.entities.CompanyInfo;
import algo.vk_monetisation.entities.Contacts;
import algo.vk_monetisation.entities.LegalEntity;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonHandler {

    private final PersonRepository personRepository;

    @Transactional
    public void addMainPerson(RequisitesDTO requisitesDTO) {
        log.info("Добавляем лицо отвечающее за компанию.");
        Person person = new Person();
        person.setName(requisitesDTO.companyInfoDTO().name());
        person.setSurname(requisitesDTO.companyInfoDTO().surname());
        person.setLastName(requisitesDTO.companyInfoDTO().lastName());
        log.debug("Лицо успешно создано");

        LegalEntityDTO legalEntityDTO = requisitesDTO.legalEntityDTO();
        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setPostalIndex(legalEntityDTO.index());
        legalEntity.setTown(legalEntityDTO.town());
        legalEntity.setAddress(legalEntityDTO.adress());
        legalEntity.setRegion(legalEntityDTO.region());
        legalEntity.setStreet(legalEntityDTO.street());
        legalEntity.setApartmentNumber(legalEntityDTO.numOfFlat());
        legalEntity.setPerson(person);
        person.setLegalEntity(legalEntity);
        log.debug("Местоположение компании добавлено.");

        ContactsDTO contactsDTO = requisitesDTO.contactsDTO();
        Contacts contacts = new Contacts();
        contacts.setContactNumber(contactsDTO.contactNumber());
        contacts.setContactPerson(contactsDTO.contactPearson());
        contacts.setPerson(person);
        person.setContacts(contacts);
        log.debug("Контакты добавлены.");

        CompanyInfoDTO companyInfoDTO = requisitesDTO.companyInfoDTO();
        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setInn(companyInfoDTO.INN());
        companyInfo.setName(companyInfoDTO.nameOfCompany());
        companyInfo.setOgrnip(companyInfoDTO.ogrnip());
        companyInfo.setPerson(person);
        person.setCompanyInfo(companyInfo);
        log.debug("Информация о компании добавлена.");

        personRepository.save(person);
    }

}
