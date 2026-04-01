package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.CampaignStatusDTO;
import algo.vk_monetisation.dto.ContentStatsDTO;
import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.managers.AdvertismentHandler;
import algo.vk_monetisation.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdvertisementService {

    private final Validator validator;

    private final AdvertismentHandler advertismentHandler;

    public void addAdvertisement(PosevDTO posevDTO)  {
        log.info("Пришел запрос на сервис на публикацию.");
        validator.validatePosev(posevDTO);
        advertismentHandler.addAdvert(posevDTO);
    }

    public List<ContentStatsDTO> getStats(Long campaignId, int pageNum) {
        log.info("Пришел запрос на сервис на получение статистики.");
        validator.validateCompaign(campaignId);
        return advertismentHandler.getStatsFromCampaign(campaignId, pageNum);
    }

    public CampaignStatusDTO getStatus(Long campaignId) {
        log.info("Пришел запрос на сервис на получение статуса.");
        validator.validateCampaignStatus(campaignId);
        return advertismentHandler.getStatusFromCampaign(campaignId);
    }

    public List<AdvertisingCampaign> getAllCampaigns(Long personId, int pageNum) {
        log.info("Пришел запрос на сервис на получение всех рекламных кампаний");
        validator.validateCampaign(personId, pageNum);
        return advertismentHandler.getAdvertisingCampaigns(personId, pageNum);
    }
}
