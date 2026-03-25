package algo.vk_monetisation.services;

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
public class AuthorContentService {

    private final AdvertisingCampaignRepository advertisingCampaignRepository;
    private final ContentRepository contentRepository;
    private final PersonRepository personRepository;

    @Transactional
    public void uploadContent(Long campaignId, MultipartFile image, MultipartFile video) {
        if (campaignId == null) {
            throw new ValidationException("campaignId не может быть null");
        }
        boolean hasImage = image != null && !image.isEmpty();
        boolean hasVideo = video != null && !video.isEmpty();
        if (!hasImage && !hasVideo) {
            throw new ValidationException("Нужно передать хотя бы один файл контента (image или video).");
        }

        AdvertisingCampaign campaign = advertisingCampaignRepository.findById(campaignId)
                .orElseThrow(() -> new ValidationException("Кампания не найдена: " + campaignId));

        // "Запуск" кампании происходит при прикреплении контента.
        // Перед выставлением ACTIVE проверяем баланс заказчика.
        if (campaign.getStatus() == null || campaign.getStatus() == AdvertisingCampaign.CampaignStatus.DRAFT) {
            var person = campaign.getPerson();
            if (person == null) {
                throw new ValidationException("У кампании не задан заказчик (person).");
            }

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

            // Списываем бюджет при запуске.
            person.setBalance(balance - required);
            personRepository.save(person);

            LocalDateTime now = LocalDateTime.now();
            campaign.setStartDate(now);
            campaign.setEndDate(now.plusMinutes(4)); // месяц от текущего времени
            campaign.setStatus(AdvertisingCampaign.CampaignStatus.ACTIVE);
        } else if (campaign.getStatus() == AdvertisingCampaign.CampaignStatus.ACTIVE) {
            // уже запущена
        } else {
            // COMPLETED или REJECTED: повторный запуск запрещён
            throw new ValidationException("Кампания не может быть активирована в текущем статусе: " + campaign.getStatus());
        }

        Content content = campaign.getContent();
        if (content == null) {
            content = new Content();
            campaign.setContent(content);
        }

        // При запуске статистика начинается с нуля.
        // Если автор перезагружает контент в ACTIVE кампании, для MVP фиксируем поведение как "сброс".
        content.setLikes(0L);
        content.setViews(0L);
        content.setCreatedAt(LocalDateTime.now());

        if (hasImage) {
            try {
                content.setImageData(image.getBytes());
            } catch (Exception e) {
                throw new ValidationException("Не удалось прочитать image байты: " + e.getMessage());
            }
            content.setImageContentType(image.getContentType());
            content.setImageFileName(image.getOriginalFilename());
        }

        if (hasVideo) {
            try {
                content.setVideoData(video.getBytes());
            } catch (Exception e) {
                throw new ValidationException("Не удалось прочитать video байты: " + e.getMessage());
            }
            content.setVideoContentType(video.getContentType());
            content.setVideoFileName(video.getOriginalFilename());
        }

        // Для MVP сериализация медиа-метаданных не критична: статистика считает likes/views.
        content.setMediaMetadata(null);

        contentRepository.save(content);
        advertisingCampaignRepository.save(campaign);

        log.info("Контент загружен для кампании {} (contentId={})", campaignId, content.getId());
    }

}

