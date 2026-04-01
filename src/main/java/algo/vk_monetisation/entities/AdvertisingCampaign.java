package algo.vk_monetisation.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "advertising_campaigns")
@Data
public class AdvertisingCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "advertisingCampaign", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Content> content;

    private String title;
    private String description;

    @Column(name = "okvd_code")
    private String okvdCode;

    @Enumerated(EnumType.STRING)
    private CampaignStatus status;

    private Double budget;

    @Column(name = "target_audience")
    private String targetAudience;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "image_data", columnDefinition = "BYTEA")
    private byte[] imageData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = CampaignStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum CampaignStatus {
        DRAFT, ACTIVE, COMPLETED, REJECTED
    }
}
