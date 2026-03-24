package algo.vk_monetisation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public record ErrorResponseDTO(
        int status,
        String error,
        String message,
        String path,
        String trace)
{ }
