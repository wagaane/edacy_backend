package sn.ods.starterkit_spring.presentation.dto.requests.utilisateur;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import sn.ods.starterkit_spring.domain.model.audit.Auditable;
import sn.ods.starterkit_spring.domain.model.utilisateur.Profile;

import java.time.LocalDate;
import java.util.Set;

/**
 * @author Abdou Karim CISSOKHO
 * @created 09/01/2025-15:45
 * @project starterkit-spring
 */

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurReqDTO {
    private String prenom;
    private String nom;
    private String email;
    private String telephone;
    private String adresse;
    private String sexe;
    private Set<Profile> profiles;
    private String lieuDeNaissance;
    private LocalDate dateNaissance;

}
