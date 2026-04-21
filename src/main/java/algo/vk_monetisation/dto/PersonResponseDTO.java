package algo.vk_monetisation.dto;

import java.time.LocalDateTime;

public record PersonResponseDTO(
        Long id,
        String name,
        String surname,
        String lastName,
        String email,
        Double balance,
        Long companyInfoId,
        Long legalEntityId,
        Long contactsId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
