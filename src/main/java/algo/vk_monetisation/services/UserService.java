package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.AuthRequestDTO;
import algo.vk_monetisation.dto.AuthResponseDTO;
import algo.vk_monetisation.entities.User;
import algo.vk_monetisation.enums.Roles;
import algo.vk_monetisation.exceptions.ValidationException;
import algo.vk_monetisation.repositories.UserRepository;
import algo.vk_monetisation.utils.JwtUtil;
import algo.vk_monetisation.utils.UserAuthMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserAuthMapper userAuthMapper;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponseDTO registerUser(AuthRequestDTO authRequestDTO) {
        log.info("Пришел запрос на сервис для регистрации пользователя.");
        if (authRequestDTO.getEmail() == null || authRequestDTO.getPassword() == null) {
            throw new ValidationException("Отсутствует пароль или email.");
        }
        String originalPassword = authRequestDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(authRequestDTO.getPassword());
        authRequestDTO.setPassword(encodedPassword);
        User newUser = userAuthMapper.toEntity(authRequestDTO);
        newUser.setRole(Roles.USER);
        userRepository.save(newUser);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.getEmail(),
                        originalPassword
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtil.generateToken(authentication);
        User user = userRepository.findByEmail(authRequestDTO.getEmail());
        return new AuthResponseDTO(accessToken, "Bearer", user.getEmail(), user.getRole() != null ? user.getRole() : Roles.USER);
    }

    @Transactional
    public AuthResponseDTO logUser(AuthRequestDTO authRequestDTO) {
        log.info("Пришел запрос на сервис для авторизации клиента.");
        if (authRequestDTO.getEmail() == null || authRequestDTO.getPassword() == null) {
            throw new ValidationException("Отсутствует пароль или email.");
        }
        User foundUser = userRepository.findByEmail(authRequestDTO.getEmail());
        if (foundUser == null || !passwordEncoder.matches(authRequestDTO.getPassword(), foundUser.getPassword())) {
            throw new ValidationException("Неверный логин или пароль.");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.getEmail(),
                        authRequestDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtil.generateToken(authentication);
        User user = userRepository.findByEmail(authRequestDTO.getEmail());
        return new AuthResponseDTO(accessToken, "Bearer", user.getEmail(), user.getRole() != null ? user.getRole() : Roles.USER);
    }

    public AuthResponseDTO regenerateTokenForUser(String email) {
        log.info("Регенерация токена для пользователя: {}", email);
        User user = userRepository.findByEmail(email);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );
        String newAccessToken = jwtUtil.generateToken(authenticationToken);
        log.info("Новый токен сгенерирован для пользователя {} с ролью {}",
                user.getEmail(), user.getRole());
        return new AuthResponseDTO(
                newAccessToken,
                "Bearer",
                user.getEmail(),
                user.getRole()
        );
    }
}
