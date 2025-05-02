package sn.wagaane.task_app.application.services.interfaces.utilisateur;

import sn.wagaane.task_app.domain.model.utilisateur.Utilisateur;
import sn.wagaane.task_app.domain.model.utilisateur.ValidationUser;

/**
 * @author Abdou Karim CISSOKHO
 * @created 17/01/2025-11:48
 * @project starterkit-spring
 */
public interface ValidationUserService {

    void validateUser(Utilisateur user);

    ValidationUser readCode(String code);
}
