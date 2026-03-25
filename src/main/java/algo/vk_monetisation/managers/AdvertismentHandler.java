package algo.vk_monetisation.managers;

import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.repositories.AdvertisingCampaignRepository;
import algo.vk_monetisation.repositories.PersonRepository;
import algo.vk_monetisation.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        campaign.setStartDate(posevDTO.startDate());
        campaign.setEndDate(posevDTO.endDate());
        campaign.setPerson(person);
        campaign.setStatus(AdvertisingCampaign.CampaignStatus.DRAFT);

        // Для совместимости с текущим DTO: храним только байты изображения (Content будет создан автором позже)
        if (posevDTO.images() != null && !posevDTO.images().isEmpty()) {
            MultipartFile mainImage = posevDTO.images().get(0);
            if (mainImage != null && !mainImage.isEmpty()) {
                try {
                    campaign.setImageData(mainImage.getBytes());
                } catch (Exception e) {
                    throw new ValidationException("Не удалось прочитать image байты: " + e.getMessage());
                }
            }
        }

        AdvertisingCampaign savedCampaign = advertisingCampaignRepository.save(campaign);
        log.info("Кампания создана: {}", savedCampaign.getId());
    }
}

