package algo.vk_monetisation.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "eis_integration_log")
@Data
@Getter @Setter
public class EisIntegrationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String inn;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "integration_point")
    private String integrationPoint;

    @Column(name = "result")
    private String result;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
