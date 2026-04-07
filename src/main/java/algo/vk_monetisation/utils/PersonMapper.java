package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.CompanyInfoDTO;
import algo.vk_monetisation.entities.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "legalEntity", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "companyInfo", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "advertisingCampaigns", ignore = true)
    Person toEntity(CompanyInfoDTO dto);
}