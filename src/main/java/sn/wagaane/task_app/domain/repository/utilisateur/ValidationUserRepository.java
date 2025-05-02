package sn.wagaane.task_app.domain.repository.utilisateur;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.wagaane.task_app.domain.model.utilisateur.ValidationUser;

import java.util.Optional;


/**
 * @author Abdou Karim CISSOKHO
 * @created 17/01/2025-11:46
 * @project starterkit-spring
 */
public interface ValidationUserRepository extends JpaRepository<ValidationUser, Long>  {

    Optional<ValidationUser> findByCode(String username);
}
