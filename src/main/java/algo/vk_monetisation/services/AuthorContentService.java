package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.CampaignActivationLedgerEntryDTO;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    private final PlatformTransactionManager transactionManager;

    private final CampaignActivationLedgerService campaignActivationLedgerService;

    @Value("${app.tx.campaign-activation-duration-minutes:4}")
    private long activationDurationMinutes;

    @Value("${app.tx.simulate-ledger-failure:false}")
    private boolean simulateLedgerFailure;

    private final int PAGE_SIZE = 10;

    public void uploadContentForCampaign(Long campaignId, MultipartFile image, MultipartFile video) {
        if (video == null && image == null) {
            log.info("выходим");
            return;
        }
        log.info("В сервис пришел запрос на добавление контета в кампанию в рамках программной JTA-транзакции.");
        validator.validateAuthorContent(campaignId, image, video);

        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        txTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);

        txTemplate.executeWithoutResult(status -> {
            AdvertisingCampaign campaign = advertisingCampaignRepository.findById(campaignId)
                    .orElseThrow(() -> new ValidationException("Кампания не найдена: " + campaignId));

            if (campaign.getStatus() == null || campaign.getStatus() == AdvertisingCampaign.CampaignStatus.DRAFT) {
                var person = campaign.getPerson();
                Double balance = person.getBalance() != null ? person.getBalance() : 0.0;
                Double required = campaign.getBudget() != null ? campaign.getBudget() : 0.0;
                if (balance < required) {
                    throw new ValidationException("Недостаточно средств для запуска кампании. balance=" + balance + ", required=" + required);
                }
                person.setBalance(balance - required);
                personRepository.save(person);
                LocalDateTime now = LocalDateTime.now();
                campaign.setStartDate(now);
                campaign.setEndDate(now.plusMinutes(activationDurationMinutes));
                campaign.setStatus(AdvertisingCampaign.CampaignStatus.ACTIVE);
            } else if (campaign.getStatus() != AdvertisingCampaign.CampaignStatus.ACTIVE) {
                throw new ValidationException("Кампания не может быть активирована в текущем статусе: " + campaign.getStatus());
            }

            Content content = new Content();
            content.setLikes(0L);
            content.setViews(0L);
            content.setCreatedAt(LocalDateTime.now());
            if (image != null) {
                try {
                    content.setImageData(image.getBytes());
                } catch (Exception e) {
                    throw new ValidationException("Не удалось прочитать image байты: " + e.getMessage());
                }
                content.setImageContentType(image.getContentType());
                content.setImageFileName(image.getOriginalFilename());
            }
            if (video != null) {
                try {
                    content.setVideoData(video.getBytes());
                } catch (Exception e) {
                    throw new ValidationException("Не удалось прочитать video байты: " + e.getMessage());
                }
                content.setVideoContentType(video.getContentType());
                content.setVideoFileName(video.getOriginalFilename());
            }
            content.setMediaMetadata(null);
            content.setAdvertisingCampaign(campaign);
            contentRepository.save(content);

            if (campaign.getContent() == null) {
                campaign.setContent(new ArrayList<>());
            }
            campaign.getContent().add(content);
            advertisingCampaignRepository.save(campaign);

            Double required = campaign.getBudget() != null ? campaign.getBudget() : 0.0;
//            try {
//                log.info("Засыпаем!!!!!!!!!!!!!");
//                Thread.sleep(50000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            campaignActivationLedgerService.recordActivation(
                    campaign.getId(),
                    campaign.getPerson().getId(),
                    content.getId(),
                    required,
                    campaign.getStatus().name()
            );

//            if (simulateLedgerFailure) {
//                throw new ValidationException("Симуляция падения после записи в ledger для проверки distributed rollback.");
//            }

            log.info("Контент загружен для кампании {} (contentId={})", campaignId, content.getId());
        });
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

    public List<CampaignActivationLedgerEntryDTO> getCampaignActivationLedger(Long campaignId) {
        validator.validateCampaign(campaignId);
        return campaignActivationLedgerService.getByCampaignId(campaignId);
    }
}

