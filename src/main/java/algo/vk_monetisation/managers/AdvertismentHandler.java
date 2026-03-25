package algo.vk_monetisation.managers;

import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.repositories.AdvertisingCampaignRepository;
import algo.vk_monetisation.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdvertismentHandler {

    private final AdvertisingCampaignRepository advertisingCampaignRepository;

    private final PersonRepository personRepository;

    private final ObjectMapper objectMapper;

    @Transactional(isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {Exception.class, RuntimeException.class})
    public void addAdvert(PosevDTO posevDTO)  {
        log.info("Выполняется транзация по добавлению рекламной кампании.");
        try {
            Person person = personRepository.findByPersonId(posevDTO.personId());
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
            if (posevDTO.images() != null && !posevDTO.images().isEmpty()) {
                MultipartFile mainImage = posevDTO.images().get(0);
                campaign.setImageData(mainImage.getBytes());
                campaign.setImageContentType(mainImage.getContentType());
                campaign.setImageFileName(mainImage.getOriginalFilename());
            }
            if (posevDTO.videos() != null && !posevDTO.videos().isEmpty()) {
                MultipartFile mainVideo = posevDTO.videos().get(0);
                campaign.setVideoData(mainVideo.getBytes());
                campaign.setVideoContentType(mainVideo.getContentType());
                campaign.setVideoFileName(mainVideo.getOriginalFilename());
            }
            String metadata = createMetadata(posevDTO);
            campaign.setMediaMetadata(metadata);
            AdvertisingCampaign savedCampaign = advertisingCampaignRepository.save(campaign);
            log.info("Кампания создана: {}", savedCampaign.getId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private String createMetadata(PosevDTO posevDTO) {
        Map<String, Object> metadata = new HashMap<>();
        List<Map<String, Object>> imagesInfo = new ArrayList<>();
        if (posevDTO.images() != null) {
            for (MultipartFile img : posevDTO.images()) {
                Map<String, Object> imgInfo = new HashMap<>();
                imgInfo.put("fileName", img.getOriginalFilename());
                imgInfo.put("contentType", img.getContentType());
                imgInfo.put("size", img.getSize());
                imagesInfo.add(imgInfo);
            }
        }
        metadata.put("images", imagesInfo);
        List<Map<String, Object>> videosInfo = new ArrayList<>();
        if (posevDTO.videos() != null) {
            for (MultipartFile video : posevDTO.videos()) {
                Map<String, Object> videoInfo = new HashMap<>();
                videoInfo.put("fileName", video.getOriginalFilename());
                videoInfo.put("contentType", video.getContentType());
                videoInfo.put("size", video.getSize());
                videosInfo.add(videoInfo);
            }
        }
        metadata.put("videos", videosInfo);
        metadata.put("createdAt", LocalDateTime.now().toString());
        return objectMapper.writeValueAsString(metadata);
    }
}

