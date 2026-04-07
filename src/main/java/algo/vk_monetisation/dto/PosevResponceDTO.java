package algo.vk_monetisation.dto;

import java.time.LocalDateTime;

public record PosevResponceDTO(
        Long id,
        String title,
        String description,
        String okvdCode,
        String status,
        Double budget,
        String targetAudience,
        LocalDateTime startDate,
        LocalDateTime endDate,
        byte[] imageData,
        Long personId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
