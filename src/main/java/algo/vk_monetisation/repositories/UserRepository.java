package algo.vk_monetisation.repositories;

import algo.vk_monetisation.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);


}
