package algo.vk_monetisation.dto;

import java.time.LocalDateTime;

public record EisIntegrationLogDTO(
        Long id,
        String provider,
        String operation,
        Long campaignId,
        String status,
        String processedByNode,
        LocalDateTime createdAt
) {
}
