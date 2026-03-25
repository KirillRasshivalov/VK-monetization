package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.ContentStatsDTO;
import algo.vk_monetisation.dto.CampaignStatusDTO;
import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.services.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/order")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @PostMapping("/advert")
    public ResponseEntity<Void> placeAd(@RequestBody PosevDTO posevDTO) {
        log.info("Пришел запрос на создание рекламной кампании.");
        advertisementService.addAdvertisement(posevDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/campaign/{campaignId}/stats")
    public ResponseEntity<ContentStatsDTO> getCampaignStats(@PathVariable Long campaignId) {
        log.info("Пришел запрос на полукчения статистики.");
        ContentStatsDTO contentStatsDTO = advertisementService.getStats(campaignId);
        return ResponseEntity.ok(contentStatsDTO);
    }

    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<CampaignStatusDTO> getCampaignStatus(@PathVariable Long campaignId) {
        log.info("Пришел запрос на получение статуса кампании.");
        CampaignStatusDTO campaignStatusDTO = advertisementService.getStatus(campaignId);
        return ResponseEntity.ok(campaignStatusDTO);
    }
}
