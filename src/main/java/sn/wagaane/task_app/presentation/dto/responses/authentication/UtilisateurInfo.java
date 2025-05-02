package sn.wagaane.task_app.presentation.dto.responses.authentication;




import sn.wagaane.task_app.domain.model.utilisateur.Profile;
import java.util.Set;


public record UtilisateurInfo(Long id, String email, String prenom, String nom, Set<Profile> profil, boolean status) {
}
