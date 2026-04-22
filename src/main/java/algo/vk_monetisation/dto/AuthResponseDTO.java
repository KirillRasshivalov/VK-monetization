package algo.vk_monetisation.dto;

import algo.vk_monetisation.enums.Roles;

public record AuthResponseDTO(
        String accessToken,
        String type,
        String email,
        Roles role
) { }
