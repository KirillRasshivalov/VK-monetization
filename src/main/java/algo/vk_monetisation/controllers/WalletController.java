package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.WalletTopUpDTO;
import algo.vk_monetisation.services.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/topup")
    @ResponseStatus(HttpStatus.OK)
    public void topUp(@RequestBody WalletTopUpDTO dto) {
        log.info("Запрос на пополнение баланса для personId={}", dto.personId());
        walletService.uppBalance(dto);
    }
}

