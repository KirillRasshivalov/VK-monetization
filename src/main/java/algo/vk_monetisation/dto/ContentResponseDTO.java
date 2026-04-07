package algo.vk_monetisation.dto;

import java.time.LocalDateTime;

public record ContentResponseDTO(
        Long id,
        String imageContentType,
        String imageFileName,
        byte[] imageData,
        byte[] videoData,
        String videoContentType,
        String videoFileName,
        String mediaMetadata,
        LocalDateTime createdAt,
        Long likes,
        Long views,
        Long finalLikes,
        Long finalViews,
        LocalDateTime statsFixedAt
) { }
