package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.CampaignStatusDTO;
import algo.vk_monetisation.dto.ContentStatsDTO;
import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.services.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/orders")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @PostMapping("/advertisement_campaign")
    @ResponseStatus(HttpStatus.OK)
    public void placeAd(@RequestBody PosevDTO posevDTO) {
        log.info("Пришел запрос на создание рекламной кампании.");
        advertisementService.addAdvertisement(posevDTO);
    }

    @GetMapping("/campaign/{campaignId}/stats")
    @ResponseStatus(HttpStatus.OK)
    public ContentStatsDTO getCampaignStats(@PathVariable Long campaignId) {
        log.info("Пришел запрос на полукчения статистики.");
        return advertisementService.getStats(campaignId);
    }

    @GetMapping("/campaign/{campaignId}")
    @ResponseStatus(HttpStatus.OK)
    public CampaignStatusDTO getCampaignStatus(@PathVariable Long campaignId) {
        log.info("Пришел запрос на получение статуса кампании.");
        return advertisementService.getStatus(campaignId);
    }
}
