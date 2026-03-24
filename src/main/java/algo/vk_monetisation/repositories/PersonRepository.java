package algo.vk_monetisation.repositories;

import algo.vk_monetisation.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

}
