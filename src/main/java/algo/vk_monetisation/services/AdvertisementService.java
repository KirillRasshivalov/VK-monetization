package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.CampaignStatusDTO;
import algo.vk_monetisation.dto.ContentStatsDTO;
import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.Content;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.exceptions.ValidationException;
import algo.vk_monetisation.repositories.AdvertisingCampaignRepository;
import algo.vk_monetisation.repositories.PersonRepository;
import algo.vk_monetisation.utils.AdvertisingCampaignMapper;
import algo.vk_monetisation.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdvertisementService {

    private final Validator validator;

    private final AdvertisingCampaignRepository advertisingCampaignRepository;

    private final PersonRepository personRepository;

    private final AdvertisingCampaignMapper advertisingCampaignMapper;

    private final ModelMapper modelMapper;

    private final int PAGE_SIZE = 10;

    @Transactional(isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {Exception.class, RuntimeException.class})
    public void addAdvertisement(PosevDTO posevDTO)  {
        log.info("Пришел запрос на сервис на публикацию.");
        validator.validatePosev(posevDTO);
        Person person = personRepository.findById(posevDTO.personId())
                .orElseThrow(() -> new ValidationException("Person не найден: " + posevDTO.personId()));
        AdvertisingCampaign campaign = new AdvertisingCampaign();
        campaign.setTitle(posevDTO.title());
        campaign.setDescription(posevDTO.description());
        campaign.setOkvdCode(posevDTO.okvdCode());
        campaign.setBudget(posevDTO.budget());
        campaign.setTargetAudience(posevDTO.targetAudience());
        campaign.setPerson(person);
        campaign.setStatus(AdvertisingCampaign.CampaignStatus.DRAFT);
        AdvertisingCampaign savedCampaign = advertisingCampaignRepository.save(campaign);
        log.info("Кампания создана: {}", savedCampaign.getId());
    }

    public List<ContentStatsDTO> getStats(Long campaignId, int pageNum) {
        log.info("Пришел запрос на сервис на получение статистики.");
        validator.validateCampaign(campaignId);
        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);
        List<Content> content = advertisingCampaignRepository.findContentsByCampaignId(campaignId, pageable);
        List<ContentStatsDTO> contentStatsDTOList = new ArrayList<>();
        for (int i = 0; i < content.size(); i++) {
            Long views = content.get(i).getFinalViews() != null ? content.get(i).getFinalViews() : content.get(i).getViews();
            Long likes = content.get(i).getFinalLikes() != null ? content.get(i).getFinalLikes() : content.get(i).getLikes();
            contentStatsDTOList.add(new ContentStatsDTO(campaignId, content.get(i).getId(), views, likes));
        }
        return contentStatsDTOList;
    }

    public CampaignStatusDTO getStatus(Long campaignId) {
        log.info("Пришел запрос на сервис на получение статуса.");
        validator.validateCampaignStatus(campaignId);
        AdvertisingCampaign campaign = advertisingCampaignRepository.findById(campaignId).get();
        Long personId = campaign.getPerson() != null ? campaign.getPerson().getId() : null;
        return new CampaignStatusDTO(
                campaignId,
                campaign.getStatus() != null ? campaign.getStatus().name() : null,
                campaign.getBudget(),
                campaign.getStartDate(),
                campaign.getEndDate(),
                personId
        );
    }

    public List<AdvertisingCampaign> getAllCampaigns(Long personId, int pageNum) {
        log.info("Пришел запрос на сервис на получение всех рекламных кампаний");
        validator.validateCampaign(personId, pageNum);
        log.info("Начало сбора всех рекламных кампаний.");
        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);
        return personRepository.findAdvertisingCampaignsByPersonId(personId, pageable);
    }

    @Transactional
    public void deleteCampaign(Long campaignId) {
        log.info("Пришел запрос на сервис на удаление кампании.");
        validator.validateCampaign(campaignId);
        advertisingCampaignRepository.deleteById(campaignId);
    }

    @Transactional
    public void updateCampaign(Long campaignId, PosevDTO posevDTO) {
        log.info("Пришел запрос на сервис на обновление рекламной кампании.");
        validator.validateCampaign(campaignId, posevDTO);
        AdvertisingCampaign oldCampaign = advertisingCampaignRepository.findById(campaignId).get();
        advertisingCampaignMapper.updateEntity(oldCampaign, posevDTO);
        advertisingCampaignRepository.save(oldCampaign);
        log.info("Обновление данных прошло успешно.");
    }
}
