package sn.ods.starterkit_spring.domain.model.utilisateur;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * @author G2k R&D
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "TP_Profile")
@SequenceGenerator(name = "seq_profile", initialValue = 100, allocationSize = 2, sequenceName = "seq_profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_profile")
    @Column(name = "pro_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Size(max = 50)
    @Column(name = "pro_code", unique = true, nullable = false, length = 10)
    private String code;

    @Size(max = 100)
    @Column(name = "pro_Libelle")
    private String label;

    @Column(name = "pro_type")
    private String typeProfile;

    @Column(name = "pro_type_division")
    private String typeProfileDivision;

    @Column(name = "pro_type_bureau")
    private String typeProfileBureau;

    @Column(name = "pro_type_direction")
    private String typeProfileDirection;



}
