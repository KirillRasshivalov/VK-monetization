package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.AuthRequestDTO;
import algo.vk_monetisation.dto.AuthResponseDTO;
import algo.vk_monetisation.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseDTO registerUser(@RequestBody AuthRequestDTO authRequestDTO) {
        log.info("Пришел запрос на регистрацию пользователя: " + authRequestDTO.getEmail());
        return userService.registerUser(authRequestDTO);
    }

    @PostMapping("/logging")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseDTO loggingUser(@RequestBody AuthRequestDTO authRequestDTO) {
        log.info("Пришел запрос на авторизацию пользователя: " + authRequestDTO.getEmail());
        return userService.logUser(authRequestDTO);
    }

}
