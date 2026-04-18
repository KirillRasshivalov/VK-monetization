package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.AuthRequestDTO;
import algo.vk_monetisation.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserAuthMapper {

    User toEntity(AuthRequestDTO authRequestDTO);
}
