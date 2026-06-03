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

    private final InnVerificationService innVerificationService;

    @Transactional
    public void createLegalEntity(LegalEntityDTO legalEntityDTO) {
        log.info("Пришел запрос на сервис для добавления информации о компании.");
        validator.validateLegalEntity(legalEntityDTO);

        log.info("Начинаем проверку ИНН компании через FNS систему");
        if (!innVerificationService.verifyInn(legalEntityDTO.inn())) {
            log.warn("INN verification failed: {}", legalEntityDTO.inn());
            throw new IllegalArgumentException("ИНН компании не прошел проверку в системе ФНС");
        }
        log.info("INN verification passed successfully");
        
        LegalEntity legalEntity = legalEntityMapper.toLegalEntity(legalEntityDTO);
        legalEntity.setInnVerificationStatus("VERIFIED");
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
        if (!legalEntity.getInn().equals(legalEntityDTO.inn())) {
            log.info("INN поменялся, начинаем повторную проверку через FNS систему");
            if (!innVerificationService.verifyInn(legalEntityDTO.inn())) {
                log.warn("New INN verification failed: {}", legalEntityDTO.inn());
                throw new IllegalArgumentException("Новый ИНН компании не прошел проверку в системе ФНС");
            }
            legalEntity.setInnVerificationStatus("VERIFIED");
        }
        
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
