package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.WalletTopUpDTO;
import algo.vk_monetisation.managers.BalanceHandler;
import algo.vk_monetisation.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final BalanceHandler balanceHandler;

    private final Validator validator;

    public void uppBalance(WalletTopUpDTO dto) {
        log.info("Пришел запрос на сервис для пополненения баланса.");
        validator.validateWalletUpper(dto);
        balanceHandler.topUp(dto);
    }
}

