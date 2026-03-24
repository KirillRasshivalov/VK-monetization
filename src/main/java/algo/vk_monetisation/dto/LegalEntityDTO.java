package algo.vk_monetisation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public record LegalEntityDTO(int index,
                             String region,
                             String town,
                             String street,
                             String adress,
                             int numOfFlat)
{ }
