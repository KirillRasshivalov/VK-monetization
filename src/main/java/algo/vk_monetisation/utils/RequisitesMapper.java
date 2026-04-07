package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.RequisitesDTO;
import algo.vk_monetisation.entities.CompanyInfo;
import algo.vk_monetisation.entities.Contacts;
import algo.vk_monetisation.entities.LegalEntity;
import algo.vk_monetisation.entities.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequisitesMapper {

    private final PersonMapper personMapper;
    private final LegalEntityMapper legalEntityMapper;
    private final ContactsMapper contactsMapper;
    private final CompanyInfoMapper companyInfoMapper;

    public Person toPersonEntity(RequisitesDTO dto) {
        Person person = personMapper.toEntity(dto.personInfoDTO());
        LegalEntity legalEntity = legalEntityMapper.toEntity(dto.legalEntityDTO());
        legalEntity.setPerson(person);
        person.setLegalEntity(legalEntity);
        Contacts contacts = contactsMapper.toEntity(dto.contactsDTO());
        contacts.setPerson(person);
        person.setContacts(contacts);
        CompanyInfo companyInfo = companyInfoMapper.toEntity(dto.companyInfoDTO());
        companyInfo.setPerson(person);
        person.setCompanyInfo(companyInfo);
        return person;
    }
}
