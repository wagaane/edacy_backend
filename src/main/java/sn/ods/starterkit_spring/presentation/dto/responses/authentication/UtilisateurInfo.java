package sn.ods.starterkit_spring.presentation.dto.responses.authentication;




import sn.ods.starterkit_spring.domain.model.utilisateur.Profile;
import java.util.Set;


public record UtilisateurInfo(Long id, String email, String prenom, String nom, Set<Profile> profil, boolean status) {
}
