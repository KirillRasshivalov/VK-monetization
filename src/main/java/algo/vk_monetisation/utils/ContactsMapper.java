package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.ContactsDTO;
import algo.vk_monetisation.dto.ContactsResponseDTO;
import algo.vk_monetisation.entities.Contacts;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContactsMapper {

    void updateContacts(@MappingTarget Contacts contacts, ContactsDTO contactsDTO);

    ContactsResponseDTO contactsToContactsDTO(Contacts contacts);
}
