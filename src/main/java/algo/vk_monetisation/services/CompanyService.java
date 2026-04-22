package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.*;
import algo.vk_monetisation.entities.*;
import algo.vk_monetisation.enums.Roles;
import algo.vk_monetisation.repositories.PersonRepository;
import algo.vk_monetisation.repositories.UserRepository;
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

    private final UserRepository userRepository;

    private final Validator validator;

    private final RequisitesMapper requisitesMapper;

    private final UserService userService;

    @Transactional
    public AuthResponseDTO addCompany(RequisitesDTO requisitesDTO) {
        log.info("Команда на создание ответственного за компанию передана в сервис.");
        log.info("ИМЕЙЛ: " + requisitesDTO.personInfoDTO().email());
        Person person = requisitesMapper.toPersonEntity(requisitesDTO);
        personRepository.save(person);
        log.info("Имейл: " + person.getEmail());
        User user = userRepository.findByEmail(person.getEmail());
        user.setRole(Roles.MODERATOR);
        userRepository.save(user);
        log.info("Лицо и все связанные данные успешно сохранены. ID: {}", person.getId());
        return userService.regenerateTokenForUser(user.getEmail());
    }
}
