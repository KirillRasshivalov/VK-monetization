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

    @Column(name = "company_name")
    private String nameOfCompany;

    @Column(name = "ogrnip", nullable = false)
    private String ogrnip;

    @Column(name = "inn_primary_dadata_ok")
    private Boolean innPrimaryDadataOk = false;

    @Column(name = "inn_secondary_dadata_ok")
    private Boolean innSecondaryDadataOk = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
}