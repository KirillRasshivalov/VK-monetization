package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.ContentDTO;
import algo.vk_monetisation.dto.ContentResponseDTO;
import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.Content;
import algo.vk_monetisation.exceptions.ValidationException;
import algo.vk_monetisation.repositories.AdvertisingCampaignRepository;
import algo.vk_monetisation.repositories.ContentRepository;
import algo.vk_monetisation.repositories.PersonRepository;
import algo.vk_monetisation.utils.ContentMapper;
import algo.vk_monetisation.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorContentService {

    private final Validator validator;

    private final AdvertisingCampaignRepository advertisingCampaignRepository;

    private final ContentRepository contentRepository;

    private final PersonRepository personRepository;

    private final ContentMapper contentMapper;

    private final int PAGE_SIZE = 10;

    @Transactional(isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {Exception.class, RuntimeException.class})
    public void uploadContentForCampaign(Long campaignId, MultipartFile image, MultipartFile video) {
        log.info("В сервис пришел запрос на добавление контета в кампанию.");
        validator.validateAuthorContent(campaignId, image, video);
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
        Content content = new Content();
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
        content.setAdvertisingCampaign(campaign);
        contentRepository.save(content);
        campaign.getContent().add(content);
        advertisingCampaignRepository.save(campaign);
        log.info("Контент загружен для кампании {} (contentId={})", campaignId, content.getId());
    }

    public List<ContentResponseDTO> getAllContentFromCampaign(Long campaignId, int pageNum) {
        log.info("Пришел запрос на сервис на получение всего контента рекламной кампании.");
        validator.validateContent(campaignId, pageNum);
        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);
        List<Content> contents = advertisingCampaignRepository.findContentsByCampaignId(campaignId, pageable);
        List<ContentResponseDTO> contentResponseDTOS = new ArrayList<>();
        for (Content content : contents) {
            contentResponseDTOS.add(contentMapper.toContentResponseDTO(content));
        }
        return contentResponseDTOS;
    }

    @Transactional
    public void updateContent(Long contentId, ContentDTO contentDTO) {
        log.info("Пришел запрос на сервис на обновление контента.");
        validator.validateContent(contentId, contentDTO);
        Content existingContent = contentRepository.findById(contentId).get();
        contentMapper.updateEntity(existingContent, contentDTO);
        contentRepository.save(existingContent);
        log.info("Изменения учпешно сохранены.");
    }

    @Transactional
    public void deleteContent(Long contentId) {
        log.info("Пришел запрос на сервис для удаления контента.");
        validator.validateContent(contentId);
        contentRepository.deleteById(contentId);
        log.info("Удаление успешно прошло.");
    }
}

