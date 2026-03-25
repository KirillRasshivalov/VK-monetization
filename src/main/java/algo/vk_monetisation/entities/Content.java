package algo.vk_monetisation.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "content")
@Data
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_content_type")
    private String imageContentType;

    @Column(name = "image_file_name")
    private String imageFileName;

    @Lob
    @Column(name = "image_data", columnDefinition = "BYTEA")
    private byte[] imageData;

    @Lob
    @Column(columnDefinition = "BYTEA")
    private byte[] videoData;

    @Column(name = "video_content_type")
    private String videoContentType;

    @Column(name = "video_file_name")
    private String videoFileName;

    @Column(columnDefinition = "jsonb")
    private String mediaMetadata;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "likes")
    private Long likes;

    @Column(name = "views")
    private Long views;

    // Зафиксированные значения на момент завершения кампании.
    @Column(name = "final_likes")
    private Long finalLikes;

    @Column(name = "final_views")
    private Long finalViews;

    @Column(name = "stats_fixed_at")
    private LocalDateTime statsFixedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        likes = (likes != null) ? likes : 0L;
        views = (views != null) ? views : 0L;
    }

}
