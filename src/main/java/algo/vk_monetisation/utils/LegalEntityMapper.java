package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.LegalEntityDTO;
import algo.vk_monetisation.dto.LegalEntityResponseDTO;
import algo.vk_monetisation.entities.LegalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LegalEntityMapper {

    LegalEntity toLegalEntity(LegalEntityDTO legalEntityDTO);

    LegalEntityResponseDTO toLegalEntityResponseDTO(LegalEntity legalEntity);

    void updateEntity(@MappingTarget LegalEntity legalEntity, LegalEntityDTO legalEntityDTO);
}
