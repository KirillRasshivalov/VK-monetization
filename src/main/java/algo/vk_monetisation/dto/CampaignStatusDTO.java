package algo.vk_monetisation.dto;

import java.time.LocalDateTime;

public record CampaignStatusDTO(
        Long campaignId,
        String status,
        Double budget,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long personId
) { }

