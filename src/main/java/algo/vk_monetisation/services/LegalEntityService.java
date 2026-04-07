package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.LegalEntityDTO;
import algo.vk_monetisation.dto.LegalEntityResponseDTO;
import algo.vk_monetisation.entities.LegalEntity;
import algo.vk_monetisation.repositories.LegalEntityRepository;
import algo.vk_monetisation.utils.LegalEntityMapper;
import algo.vk_monetisation.utils.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LegalEntityService {

    private final LegalEntityMapper legalEntityMapper;

    private final LegalEntityRepository legalEntityRepository;

    private final Validator validator;

    @Transactional
    public void createLegalEntity(LegalEntityDTO legalEntityDTO) {
        log.info("Пришел запрос на сервис для добавления информации о компании.");
        validator.validateLegalEntity(legalEntityDTO);
        LegalEntity legalEntity = legalEntityMapper.toLegalEntity(legalEntityDTO);
        legalEntityRepository.save(legalEntity);
    }

    public LegalEntityResponseDTO getLegalEntity(Long id) {
        log.info("Пришел запрос на сервис для просмотра инфы о компании.");
        validator.validateLegalEntity(id);
        LegalEntity legalEntity = legalEntityRepository.findById(id).get();
        return legalEntityMapper.toLegalEntityResponseDTO(legalEntity);
    }

    @Transactional
    public void updateLegalEntity(Long id, LegalEntityDTO legalEntityDTO) {
        log.info("Пришел запрос на сервис для обновления информации о компании.");
        validator.validateLegalEntity(id, legalEntityDTO);
        LegalEntity legalEntity = legalEntityRepository.findById(id).get();
        legalEntityMapper.updateEntity(legalEntity, legalEntityDTO);
        legalEntityRepository.save(legalEntity);
    }

    @Transactional
    public void deleteLegalEntity(Long id) {
        log.info("Пришел запрос на сервис на удаление информации о компании.");
        validator.validateLegalEntity(id);
        legalEntityRepository.deleteById(id);
    }

}
