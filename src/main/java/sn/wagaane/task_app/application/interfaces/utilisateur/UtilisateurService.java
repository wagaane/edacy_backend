package sn.wagaane.task_app.application.services.interfaces.utilisateur;




import sn.wagaane.task_app.domain.model.utilisateur.Utilisateur;
import sn.wagaane.task_app.presentation.dto.requests.utilisateur.UserReqForAdminDTO;
import sn.wagaane.task_app.presentation.dto.requests.utilisateur.UserReqForUserDTO;
import sn.wagaane.task_app.presentation.dto.responses.Response;

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
