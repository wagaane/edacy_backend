package sn.ods.starterkit_spring.application.services.interfaces.utilisateur;

import sn.ods.starterkit_spring.domain.model.utilisateur.Utilisateur;
import sn.ods.starterkit_spring.domain.model.utilisateur.ValidationUser;

/**
 * @author Abdou Karim CISSOKHO
 * @created 17/01/2025-11:48
 * @project starterkit-spring
 */
public interface ValidationUserService {

    void validateUser(Utilisateur user);

    ValidationUser readCode(String code);
}
