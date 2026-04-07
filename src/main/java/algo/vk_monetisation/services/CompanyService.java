package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.CompanyInfoDTO;
import algo.vk_monetisation.dto.ContactsDTO;
import algo.vk_monetisation.dto.LegalEntityDTO;
import algo.vk_monetisation.dto.RequisitesDTO;
import algo.vk_monetisation.entities.CompanyInfo;
import algo.vk_monetisation.entities.Contacts;
import algo.vk_monetisation.entities.LegalEntity;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.repositories.PersonRepository;
import algo.vk_monetisation.utils.RequisitesMapper;
import algo.vk_monetisation.utils.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final PersonRepository personRepository;

    private final Validator validator;

    private final RequisitesMapper requisitesMapper;

    @Transactional
    public void addCompany(RequisitesDTO requisitesDTO) {
        log.info("Команда на создание ответственного за компанию передана в сервис.");
        validator.validateRequisites(requisitesDTO);
        Person person = requisitesMapper.toPersonEntity(requisitesDTO);
        personRepository.save(person);
        log.info("Лицо и все связанные данные успешно сохранены. ID: {}", person.getId());
    }
}
