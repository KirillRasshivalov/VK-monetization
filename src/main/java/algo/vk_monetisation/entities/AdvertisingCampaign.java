package algo.vk_monetisation.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "advertising_campaigns")
@Data
public class AdvertisingCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "content_id", nullable = true)
    private Content content;

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

//    @Column(name = "image_content_type")
//    private String imageContentType;
//
//    @Column(name = "image_file_name")
//    private String imageFileName;
//
//    @Lob
//    @Column(columnDefinition = "BYTEA")
//    private byte[] videoData;
//
//    @Column(name = "video_content_type")
//    private String videoContentType;
//
//    @Column(name = "video_file_name")
//    private String videoFileName;
//
//    @Column(columnDefinition = "jsonb")
//    private String mediaMetadata;

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
