package algo.vk_monetisation.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company_info")
@Data
@Getter @Setter
public class CompanyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String inn;

    private String name;

    @Column(name = "ogrnip")
    private String ogrnip;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
}