package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.CompanyInfoDTO;
import algo.vk_monetisation.dto.PersonDTO;
import algo.vk_monetisation.dto.PersonInfoDTO;
import algo.vk_monetisation.dto.PersonResponseDTO;
import algo.vk_monetisation.entities.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "legalEntity", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "companyInfo", ignore = true)
    Person toEntity(PersonInfoDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "legalEntity", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "companyInfo", ignore = true)
    Person toEntity(PersonDTO personDTO);

    PersonResponseDTO toDTO(Person person);

    void updateEntity(@MappingTarget Person person, PersonDTO dto);
}