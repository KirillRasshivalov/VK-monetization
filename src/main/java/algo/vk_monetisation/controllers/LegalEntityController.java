package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.LegalEntityDTO;
import algo.vk_monetisation.dto.LegalEntityResponseDTO;
import algo.vk_monetisation.services.LegalEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/legal_entities")
public class LegalEntityController {

    private final LegalEntityService legalEntityService;

    @PostMapping("/create_leegal_entities")
    @ResponseStatus(HttpStatus.OK)
    public void createLegalEntity(@RequestBody LegalEntityDTO legalEntityDTO) {
        log.info("Пришел запрос на добавление информации о компании.");
        legalEntityService.createLegalEntity(legalEntityDTO);
    }

    @GetMapping("/get_legal_entity/{legalId}")
    @ResponseStatus(HttpStatus.OK)
    public LegalEntityResponseDTO getLegalEntity(@PathVariable Long legalId) {
        log.info("Пришел запрос на показ информации о компании.");
        return legalEntityService.getLegalEntity(legalId);
    }

    @PutMapping("/update_legal_entity/{legalId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateLegalEntity(@PathVariable Long legalId, @RequestBody LegalEntityDTO legalEntityDTO) {
        log.info("Пришел запрос на обновление информации о компании.");
        legalEntityService.updateLegalEntity(legalId, legalEntityDTO);
    }

    @DeleteMapping("/delete_legal_entity/{legalId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLegalEntity(@PathVariable Long legalId) {
        log.info("Пришел запрос на удаление информации о компании.");
        legalEntityService.deleteLegalEntity(legalId);
    }
}
