package algo.vk_monetisation.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
@Slf4j
public class PingController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String Ping() {
        log.info("Ping request received");
        return "PONG";
    }
}
