package algo.vk_monetisation.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "legal_entities")
@Data
@Getter @Setter
public class LegalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "postal_index")
    private Integer postalIndex;

    private String region;
    private String town;
    private String street;
    private String address;

    @Column(name = "apartment_number")
    private Integer apartmentNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
}
