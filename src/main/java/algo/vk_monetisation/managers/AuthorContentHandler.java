package algo.vk_monetisation.managers;

import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.Content;
import algo.vk_monetisation.exceptions.ValidationException;
import algo.vk_monetisation.repositories.AdvertisingCampaignRepository;
import algo.vk_monetisation.repositories.ContentRepository;
import algo.vk_monetisation.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorContentHandler {

    private final AdvertisingCampaignRepository advertisingCampaignRepository;
    private final ContentRepository contentRepository;
    private final PersonRepository personRepository;

    @Transactional
    public void uploadContent(Long campaignId, MultipartFile image, MultipartFile video) {

        AdvertisingCampaign campaign = advertisingCampaignRepository.findById(campaignId).get();

        if (campaign.getStatus() == null || campaign.getStatus() == AdvertisingCampaign.CampaignStatus.DRAFT) {
            var person = campaign.getPerson();

            Double balance = person.getBalance();
            if (balance == null) {
                balance = 0.0;
            }
            Double required = campaign.getBudget();
            if (required == null) {
                required = 0.0;
            }

            if (balance < required) {
                campaign.setStatus(AdvertisingCampaign.CampaignStatus.REJECTED);
                advertisingCampaignRepository.save(campaign);
                throw new ValidationException("Недостаточно средств для запуска кампании. balance=" + balance + ", required=" + required);
            }
            person.setBalance(balance - required);
            personRepository.save(person);
            LocalDateTime now = LocalDateTime.now();
            campaign.setStartDate(now);
            campaign.setEndDate(now.plusMinutes(4));
            campaign.setStatus(AdvertisingCampaign.CampaignStatus.ACTIVE);
        } else if (campaign.getStatus() == AdvertisingCampaign.CampaignStatus.ACTIVE) {
        } else {
            throw new ValidationException("Кампания не может быть активирована в текущем статусе: " + campaign.getStatus());
        }

        Content content = campaign.getContent();
        if (content == null) {
            content = new Content();
            campaign.setContent(content);
        }
        content.setLikes(0L);
        content.setViews(0L);
        content.setCreatedAt(LocalDateTime.now());
        try {
            content.setImageData(image.getBytes());
        } catch (Exception e) {
            throw new ValidationException("Не удалось прочитать image байты: " + e.getMessage());
        }
        content.setImageContentType(image.getContentType());
        content.setImageFileName(image.getOriginalFilename());
        try {
            content.setVideoData(video.getBytes());
        } catch (Exception e) {
            throw new ValidationException("Не удалось прочитать video байты: " + e.getMessage());
        }
        content.setVideoContentType(video.getContentType());
        content.setVideoFileName(video.getOriginalFilename());
        content.setMediaMetadata(null);
        contentRepository.save(content);
        advertisingCampaignRepository.save(campaign);
        log.info("Контент загружен для кампании {} (contentId={})", campaignId, content.getId());
    }

}
