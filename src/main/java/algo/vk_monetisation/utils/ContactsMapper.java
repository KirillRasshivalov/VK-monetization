package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.ContactsDTO;
import algo.vk_monetisation.dto.ContactsResponseDTO;
import algo.vk_monetisation.entities.Contacts;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContactsMapper {

    void updateContacts(@MappingTarget Contacts contacts, ContactsDTO contactsDTO);

    ContactsResponseDTO contactsToContactsDTO(Contacts contacts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(source = "contactPerson", target = "contactPerson")
    @Mapping(source = "contactNumber", target = "contactNumber")
    Contacts toEntity(ContactsDTO dto);
}
