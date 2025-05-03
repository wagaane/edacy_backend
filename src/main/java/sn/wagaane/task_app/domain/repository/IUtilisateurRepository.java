package sn.wagaane.task_app.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import sn.wagaane.task_app.domain.model.utilisateur.Utilisateur;

import java.util.Optional;



public interface IUtilisateurRepository
        extends JpaRepository<Utilisateur, Long>, QuerydslPredicateExecutor<Utilisateur> {
    Optional<Utilisateur> findByEmailAndDeletedFalse(String username);

/*
    @Query("SELECT u FROM Utilisateur u JOIN u.profils p WHERE p.code IN :profileCodes")
    List<Utilisateur> findByProfileCodes(List<String> profileCodes);
*/

}
