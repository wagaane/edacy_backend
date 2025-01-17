package sn.ods.starterkit_spring.application.services.interfaces.utilisateur;




import sn.ods.starterkit_spring.domain.model.utilisateur.Utilisateur;
import sn.ods.starterkit_spring.presentation.dto.requests.utilisateur.UtilisateurReqDTO;
import sn.ods.starterkit_spring.presentation.dto.responses.APIResponse;
import sn.ods.starterkit_spring.presentation.dto.responses.Response;

/**
 * @author Abdou Karim CISSOKHO
 * @created 07/01/2025-13:01
 * @project starterkit-spring
 */

public interface UtilisateurService {


    Utilisateur createUser(UtilisateurReqDTO dto);
    Utilisateur updateUser(Long id,UtilisateurReqDTO dto);
    Utilisateur getUser(Long id);

    Response<Object> getUserPage(int page, int size, String filter);


}