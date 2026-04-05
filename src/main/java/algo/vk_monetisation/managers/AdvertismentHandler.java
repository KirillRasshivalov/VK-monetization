package algo.vk_monetisation.managers;

import algo.vk_monetisation.dto.CampaignStatusDTO;
import algo.vk_monetisation.dto.ContentStatsDTO;
import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.Content;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.exceptions.ValidationException;
import algo.vk_monetisation.repositories.AdvertisingCampaignRepository;
import algo.vk_monetisation.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdvertismentHandler {

    private final AdvertisingCampaignRepository advertisingCampaignRepository;

    private final PersonRepository personRepository;

    private final ModelMapper modelMapper;

    private final int PAGE_SIZE = 10;

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

    public List<ContentStatsDTO> getStatsFromCampaign(Long id, int pageNum) {
        log.info("Пришел запрос на вывод статистики для всего контента рекламной кампании.");
        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);
        List<Content> content = advertisingCampaignRepository.findContentsByCampaignId(id, pageable);
        List<ContentStatsDTO> contentStatsDTOList = new ArrayList<>();
        for (int i = 0; i < content.size(); i++) {
            Long views = content.get(i).getFinalViews() != null ? content.get(i).getFinalViews() : content.get(i).getViews();
            Long likes = content.get(i).getFinalLikes() != null ? content.get(i).getFinalLikes() : content.get(i).getLikes();
            contentStatsDTOList.add(new ContentStatsDTO(id, content.get(i).getId(), views, likes));
        }
        return contentStatsDTOList;
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

    public List<Content> shawAllContent(Long id, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);
        return advertisingCampaignRepository.findContentsByCampaignId(id, pageable);
    }

    public List<AdvertisingCampaign> getAdvertisingCampaigns(Long id, int pageNum) {
        log.info("Начало сбора всех рекламных кампаний.");
        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);
        return personRepository.findAdvertisingCampaignsByPersonId(id, pageable);
    }

    public void deleteCampaign(Long campaignId) {
        log.info("Начало удаление рекламной кампании.");
        advertisingCampaignRepository.deleteById(campaignId);
    }

    public void updateCampaign(Long campaignId, AdvertisingCampaign advertisingCampaign) {
        log.info("Начало обновления рекламной кампании.");
        AdvertisingCampaign oldCampaign = advertisingCampaignRepository.findById(campaignId).get();
        modelMapper.map(advertisingCampaign, oldCampaign);
        advertisingCampaignRepository.save(oldCampaign);
        log.info("Обновление данных прошло успешно.");
    }
}

