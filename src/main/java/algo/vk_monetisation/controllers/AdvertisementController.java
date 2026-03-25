package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.services.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/order")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @PostMapping("/advert")
    public ResponseEntity<Void> placeAd(@RequestBody PosevDTO posevDTO) {
        log.info("Пришел запрос на создание посева.");
        advertisementService.addAdvertisement(posevDTO);
        return ResponseEntity.ok().build();
    }
}
