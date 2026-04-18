package algo.vk_monetisation.dto;

public record AuthResponseDTO(
        String accessToken,
        String type,
        String email,
        String role
) { }
