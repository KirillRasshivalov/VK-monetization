package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.RequisitesDTO;
import algo.vk_monetisation.managers.PersonHandler;
import algo.vk_monetisation.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final PersonHandler personHandler;

    private final Validator validator;

    public void addCompany(RequisitesDTO requisitesDTO) {
        validator.validateRequisites(requisitesDTO);
        log.info("Команда на создание ответсвтенного за компанию передана в сервис.");
        personHandler.addMainPerson(requisitesDTO);
    }
}
