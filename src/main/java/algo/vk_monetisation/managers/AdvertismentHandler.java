package algo.vk_monetisation.managers;

import algo.vk_monetisation.dto.CampaignStatusDTO;
import algo.vk_monetisation.dto.ContentStatsDTO;
import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.Content;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.repositories.AdvertisingCampaignRepository;
import algo.vk_monetisation.repositories.PersonRepository;
import algo.vk_monetisation.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdvertismentHandler {

    private final AdvertisingCampaignRepository advertisingCampaignRepository;

    private final PersonRepository personRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {Exception.class, RuntimeException.class})
    public void addAdvert(PosevDTO posevDTO)  {
        log.info("Выполняется транзация по добавлению рекламной кампании.");
        Person person = personRepository.findById(posevDTO.personId())
                .orElseThrow(() -> new ValidationException("Person не найден: " + posevDTO.personId()));
        AdvertisingCampaign campaign = new AdvertisingCampaign();
        campaign.setTitle(posevDTO.title());
        campaign.setDescription(posevDTO.description());
        campaign.setOkvdCode(posevDTO.OKVDCode());
        campaign.setBudget(posevDTO.budget());
        campaign.setTargetAudience(posevDTO.targetAudience());
        campaign.setPerson(person);
        campaign.setStatus(AdvertisingCampaign.CampaignStatus.DRAFT);
        AdvertisingCampaign savedCampaign = advertisingCampaignRepository.save(campaign);
        log.info("Кампания создана: {}", savedCampaign.getId());
    }

    public ContentStatsDTO getStatsFromCompaign(Long id) {
        AdvertisingCampaign campaign = advertisingCampaignRepository.findById(id).get();
        Content content = campaign.getContent();
        Long views = content.getFinalViews() != null ? content.getFinalViews() : content.getViews();
        Long likes = content.getFinalLikes() != null ? content.getFinalLikes() : content.getLikes();
        return new ContentStatsDTO(id, content.getId(), views, likes);
    }

    public CampaignStatusDTO getStatusFromCampaign(Long id) {
        AdvertisingCampaign campaign = advertisingCampaignRepository.findById(id).get();
        Long personId = campaign.getPerson() != null ? campaign.getPerson().getId() : null;
        return new CampaignStatusDTO(
                id,
                campaign.getStatus() != null ? campaign.getStatus().name() : null,
                campaign.getBudget(),
                campaign.getStartDate(),
                campaign.getEndDate(),
                personId
                );
    }
}

