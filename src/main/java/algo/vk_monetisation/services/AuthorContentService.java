package algo.vk_monetisation.services;

import algo.vk_monetisation.managers.AuthorContentHandler;
import algo.vk_monetisation.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorContentService {

    private final AuthorContentHandler authorContentHandler;

    private final Validator validator;

    public void uploadContentForCampaign(Long campaignId, MultipartFile image, MultipartFile video) {
        log.info("В сервис пришел запрос на добавление контета в кампанию.");
        validator.validateAuthorContent(campaignId, image, video);
        authorContentHandler.uploadContent(campaignId, image, video);
    }

}

