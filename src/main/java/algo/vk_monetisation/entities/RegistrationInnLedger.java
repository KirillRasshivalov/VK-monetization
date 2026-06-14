package algo.vk_monetisation.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration_inn_ledger")
@Data
@Getter @Setter
public class RegistrationInnLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String inn;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "primary_checked")
    private Boolean primaryChecked = false;

    @Column(name = "secondary_checked")
    private Boolean secondaryChecked = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
