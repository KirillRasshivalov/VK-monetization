package algo.vk_monetisation.dto;

public record LegalEntityResponseDTO(
        Long id,
        Integer postalIndex,
        String region,
        String town,
        String street,
        String address,
        Integer apartmentNumber
) { }
