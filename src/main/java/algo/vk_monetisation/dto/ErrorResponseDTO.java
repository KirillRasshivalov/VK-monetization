package algo.vk_monetisation.dto;

import lombok.Builder;


@Builder
public record ErrorResponseDTO(
        int status,
        String error,
        String message,
        String path,
        String trace
) { }
