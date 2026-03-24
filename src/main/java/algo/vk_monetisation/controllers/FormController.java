package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.RequisitesDTO;
import algo.vk_monetisation.services.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
@Slf4j
public class FormController {

    private final CompanyService companyService;

    @PostMapping("/company")
    public ResponseEntity<Void> registerCompany(@RequestBody RequisitesDTO requisitesDTO) {
        log.info("Пришел запрос на регистрацию компании.");
        companyService.addCompany(requisitesDTO);
        return ResponseEntity.ok().build();
    }
}
