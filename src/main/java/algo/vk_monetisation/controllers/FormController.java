package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.AuthResponseDTO;
import algo.vk_monetisation.dto.RequisitesDTO;
import algo.vk_monetisation.services.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
@Slf4j
public class FormController {

    private final CompanyService companyService;

    @PostMapping("/company")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR')")
    public AuthResponseDTO registerCompany(@RequestBody RequisitesDTO requisitesDTO) {
        log.info("Пришел запрос на регистрацию компании.");
        return companyService.addCompany(requisitesDTO);
    }
}
