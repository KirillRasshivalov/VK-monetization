package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.PosevDTO;
import algo.vk_monetisation.entities.AdvertisingCampaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdvertisingCampaignMapper {

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget AdvertisingCampaign target, PosevDTO source);
}
