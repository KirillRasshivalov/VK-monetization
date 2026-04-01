package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.CampaignStatusDTO;
import algo.vk_monetisation.dto.ContentStatsDTO;
import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.services.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/campaign/{campaignId}")
    @ResponseStatus(HttpStatus.OK)
    public CampaignStatusDTO getCampaignStatus(@PathVariable Long campaignId) {
        log.info("Пришел запрос на получение статуса кампании.");
        return advertisementService.getStatus(campaignId);
    }

    @GetMapping("/campaign/{campaignId}/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ContentStatsDTO> getCampaignStats(@PathVariable Long campaignId, @RequestParam int pageNum) {
        log.info("Пришел запрос на получение статистики.");
        return advertisementService.getStats(campaignId, pageNum);
    }

    @GetMapping("/shaw_all_campaigns/{personId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AdvertisingCampaign> getAllCampaigns(@PathVariable Long personId, @RequestParam int pageNum) {
        log.info("Пришел запрос на просмотр всех рекламных кампаний ответственного за продвижение лица.");
        return advertisementService.getAllCampaigns(personId, pageNum);
    }

    @DeleteMapping("/delete_campaign/{campaignId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCampaign(@PathVariable Long campaignId) {
        log.info("Пришел запрос на удаление кампании.");
        return ;
    }

    @PutMapping("/update_campaign/{campaignId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateCampaign(@PathVariable Long campaignId, @RequestBody AdvertisingCampaign advertisingCampaign) {
        log.info("Пришел запрос на обновление кампании.");
        return ;
    }

}
