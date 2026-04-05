package algo.vk_monetisation.repositories;

import algo.vk_monetisation.entities.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactsRepository extends JpaRepository<Contacts, Integer> {

    Contacts findById(Long contactId);
}
