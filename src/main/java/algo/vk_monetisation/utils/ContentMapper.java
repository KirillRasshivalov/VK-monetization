package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.ContentDTO;
import algo.vk_monetisation.dto.ContentResponseDTO;
import algo.vk_monetisation.entities.Content;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

@Mapper(componentModel = "spring")
public interface ContentMapper {

    void updateEntity(@MappingTarget Content content, ContentDTO contentDTO);

    ContentResponseDTO toContentResponseDTO(Content content);
}
