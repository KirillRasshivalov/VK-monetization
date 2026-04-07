package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.ContentDTO;
import algo.vk_monetisation.dto.ContentResponseDTO;
import algo.vk_monetisation.services.AuthorContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/author")
public class AuthorContentController {

    private final AuthorContentService authorContentService;

    @GetMapping("/get_all_content_campaigns/{campaignId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ContentResponseDTO> getAllCampaignsContent(@PathVariable Long campaignId, @RequestParam int pageNum) {
        log.info("Пришел запрос на получение всего контента одной рекламной кампании: " + campaignId);
        return authorContentService.getAllContentFromCampaign(campaignId, pageNum);
    }

    @PutMapping("/update_content/{contentId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateContent(@PathVariable Long contentId, @RequestBody ContentDTO contentDTO) {
        log.info("Пришел запрос на обновление контента.");
        authorContentService.updateContent(contentId, contentDTO);
    }

    @DeleteMapping("/delete_content/{contentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteContent(@PathVariable Long contentId) {
        log.info("Пришел запрос на удаление контента.");
        authorContentService.deleteContent(contentId);
    }

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

