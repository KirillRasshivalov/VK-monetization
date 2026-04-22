package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.CampaignStatusDTO;
import algo.vk_monetisation.dto.ContentStatsDTO;
import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.dto.PosevResponceDTO;
import algo.vk_monetisation.services.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('MODERATOR')")
    public void placeAd(@RequestBody PosevDTO posevDTO) {
        log.info("Пришел запрос на создание рекламной кампании.");
        advertisementService.addAdvertisement(posevDTO);
    }

    @GetMapping("/campaign/{campaignId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MODERATOR')")
    public CampaignStatusDTO getCampaignStatus(@PathVariable Long campaignId) {
        log.info("Пришел запрос на получение статуса кампании.");
        return advertisementService.getStatus(campaignId);
    }

    @GetMapping("/campaign/{campaignId}/stats")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MODERATOR')")
    public List<ContentStatsDTO> getCampaignStats(@PathVariable Long campaignId, @RequestParam int pageNum) {
        log.info("Пришел запрос на получение статистики.");
        return advertisementService.getStats(campaignId, pageNum);
    }

    @GetMapping("/all_campaigns/{personId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MODERATOR')")
    public List<PosevResponceDTO> getAllCampaigns(@PathVariable Long personId, @RequestParam int pageNum) {
        log.info("Пришел запрос на просмотр всех рекламных кампаний ответственного за продвижение лица.");
        return advertisementService.getAllCampaigns(personId, pageNum);
    }

    @DeleteMapping("/delete_campaign/{campaignId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MODERATOR')")
    public void deleteCampaign(@PathVariable Long campaignId) {
        log.info("Пришел запрос на удаление кампании.");
        advertisementService.deleteCampaign(campaignId);
    }

    @PutMapping("/update_campaign/{campaignId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MODERATOR')")
    public void updateCampaign(@PathVariable Long campaignId, @RequestBody PosevDTO posevDTO) {
        log.info("Пришел запрос на обновление кампании.");
        advertisementService.updateCampaign(campaignId, posevDTO);
    }

}
