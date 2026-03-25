package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.ContentStatsDTO;
import algo.vk_monetisation.dto.CampaignStatusDTO;
import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.repositories.AdvertisingCampaignRepository;
import algo.vk_monetisation.services.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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

    private final AdvertisingCampaignRepository advertisingCampaignRepository;

    @PostMapping("/advert")
    public ResponseEntity<Void> placeAd(@RequestBody PosevDTO posevDTO) {
        log.info("Пришел запрос на создание рекламной кампании.");
        advertisementService.addAdvertisement(posevDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/campaign/{campaignId}/stats")
    public ResponseEntity<ContentStatsDTO> getCampaignStats(@PathVariable Long campaignId) {
        var campaign = advertisingCampaignRepository.findById(campaignId).orElse(null);
        if (campaign == null || campaign.getContent() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var content = campaign.getContent();

        Long views = content.getFinalViews() != null ? content.getFinalViews() : content.getViews();
        Long likes = content.getFinalLikes() != null ? content.getFinalLikes() : content.getLikes();

        return ResponseEntity.ok().body(new ContentStatsDTO(
                campaignId,
                content.getId(),
                views,
                likes
        ));
    }

    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<CampaignStatusDTO> getCampaignStatus(@PathVariable Long campaignId) {
        var campaign = advertisingCampaignRepository.findById(campaignId).orElse(null);
        if (campaign == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Long personId = campaign.getPerson() != null ? campaign.getPerson().getId() : null;
        return ResponseEntity.ok(new CampaignStatusDTO(
                campaignId,
                campaign.getStatus() != null ? campaign.getStatus().name() : null,
                campaign.getBudget(),
                campaign.getStartDate(),
                campaign.getEndDate(),
                personId
        ));
    }

}
