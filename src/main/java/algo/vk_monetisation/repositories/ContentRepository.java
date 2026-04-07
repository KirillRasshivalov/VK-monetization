package algo.vk_monetisation.repositories;

import algo.vk_monetisation.entities.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long> {
    Optional<Content> findById(Long id);
}

