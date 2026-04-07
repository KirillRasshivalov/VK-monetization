package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.CompanyInfoDTO;
import algo.vk_monetisation.entities.CompanyInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyInfoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    CompanyInfo toEntity(CompanyInfoDTO dto);
}
