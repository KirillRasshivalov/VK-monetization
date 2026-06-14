package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.*;
import algo.vk_monetisation.entities.User;
import algo.vk_monetisation.enums.Roles;
import algo.vk_monetisation.managers.PersonHandler;
import algo.vk_monetisation.repositories.UserRepository;
import algo.vk_monetisation.utils.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final UserRepository userRepository;

    private final UserService userService;

    private final PersonHandler personHandler;
    private final Validator validator;

    @Transactional
    public AuthResponseDTO addCompany(RequisitesDTO requisitesDTO) {
        log.info("Команда на создание ответственного за компанию передана в сервис.");
        validator.validateRequisites(requisitesDTO);

        if (requisitesDTO.personInfoDTO() == null || requisitesDTO.personInfoDTO().email() == null) {
            throw new IllegalArgumentException("personInfoDTO.email обязателен для регистрации компании.");
        }

        String email = requisitesDTO.personInfoDTO().email();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден: " + email);
        }

        personHandler.addMainPerson(requisitesDTO);
        log.info("Асинхронная проверка ИНН поставлена в Kafka для {}", requisitesDTO.companyInfoDTO().inn());

        user.setRole(Roles.MODERATOR);
        userRepository.save(user);
        log.info("Права пользователя {} обновлены до MODERATOR.", email);
        return userService.regenerateTokenForUser(email);
    }
}
