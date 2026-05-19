package algo.vk_monetisation.repositories;

import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.Person;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Override
    Optional<Person> findById(Long aLong);

    @Query("SELECT DISTINCT ac FROM AdvertisingCampaign ac " +
            "LEFT JOIN FETCH ac.person " +
            "WHERE ac.person.id = :personId")
    List<AdvertisingCampaign> findAdvertisingCampaignsByPersonId(@Param("personId") Long id, Pageable pageable);

    @Query("SELECT p FROM Person p " +
            "LEFT JOIN FETCH p.companyInfo " +
            "LEFT JOIN FETCH p.contacts " +
            "WHERE p.id = :id")
    Optional<Person> findWithDetailsById(@Param("id") Long id);
}
