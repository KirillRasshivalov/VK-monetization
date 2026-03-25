package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.managers.AdvertismentHandler;
import algo.vk_monetisation.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdvertisementService {

    private final Validator validator;

    private final AdvertismentHandler advertismentHandler;

    public void addAdvertisement(PosevDTO posevDTO)  {
        log.info("Пришел запрос на публикацию посева.");
        validator.validatePosev(posevDTO);
        advertismentHandler.addAdvert(posevDTO);

    }
}
