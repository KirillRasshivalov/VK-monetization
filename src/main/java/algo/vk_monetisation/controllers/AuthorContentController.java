package algo.vk_monetisation.controllers;

import algo.vk_monetisation.services.AuthorContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/author")
public class AuthorContentController {

    private final AuthorContentService authorContentService;

    @PostMapping("/content")
    @ResponseStatus(HttpStatus.OK)
    public void uploadContent(
            @RequestParam("campaignId") Long campaignId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "video", required = false) MultipartFile video
    ) {
        log.info("Запрос на загрузку контента для кампании {}", campaignId);
        authorContentService.uploadContentForCampaign(campaignId, image, video);
    }
}

