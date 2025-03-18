package sn.ods.starterkit_spring.application.services.interfaces.utilisateur;




import sn.ods.starterkit_spring.domain.model.utilisateur.Utilisateur;
import sn.ods.starterkit_spring.presentation.dto.requests.utilisateur.UserReqForAdminDTO;
import sn.ods.starterkit_spring.presentation.dto.requests.utilisateur.UserReqForUserDTO;
import sn.ods.starterkit_spring.presentation.dto.responses.Response;

import java.util.Map;

/**
 * @author Abdou Karim CISSOKHO
 * @created 07/01/2025-13:01
 * @project starterkit-spring
 */

public interface UtilisateurService {


    Utilisateur createUserFromAdmin(UserReqForAdminDTO dto);
    Utilisateur createUserFromUser(UserReqForUserDTO dto);
    Utilisateur updateUser(Long id, UserReqForAdminDTO dto);
    Utilisateur getUser(Long id);

    Response<Object> getUserPage(int page, int size, String filter);

    void activation(Map<String, String> activation);

}