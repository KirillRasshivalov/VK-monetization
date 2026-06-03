package algo.vk_monetisation.dto;


public record LegalEntityDTO(
        Integer postalIndex,
        String region,
        String town,
        String street,
        String address,
        Integer apartmentNumber,
        String inn
) { }
