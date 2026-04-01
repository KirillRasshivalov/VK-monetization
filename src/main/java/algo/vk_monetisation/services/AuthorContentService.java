package algo.vk_monetisation.services;

import algo.vk_monetisation.entities.Content;
import algo.vk_monetisation.managers.AdvertismentHandler;
import algo.vk_monetisation.managers.AuthorContentHandler;
import algo.vk_monetisation.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorContentService {

    private final AuthorContentHandler authorContentHandler;

    private final Validator validator;

    private final AdvertismentHandler advertismentHandler;

    public void uploadContentForCampaign(Long campaignId, MultipartFile image, MultipartFile video) {
        log.info("В сервис пришел запрос на добавление контета в кампанию.");
        validator.validateAuthorContent(campaignId, image, video);
        authorContentHandler.uploadContent(campaignId, image, video);
    }

    public List<Content> getAllContentFromCampaign(Long campaignId, int pageNum) {
        log.info("Пришел запрос на сервис на получение всего контента рекламной кампании.");
        validator.validateContent(campaignId, pageNum);
        return advertismentHandler.shawAllContent(campaignId, pageNum);
    }

    public void updateContent(Long contentId, Content content) {
        log.info("Пришел запрос на сервис на обновление контента.");
        validator.validateContent(contentId, content);
        authorContentHandler.updateContent(contentId, content);
    }

    public void deleteContent(Long contentId) {
        log.info("Пришел запрос на сервис для удаления контента.");
        validator.validateContent(contentId);
        authorContentHandler.deleteContent(contentId);
    }
}

