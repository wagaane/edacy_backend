package sn.ods.starterkit_spring.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import sn.ods.starterkit_spring.domain.model.utilisateur.Utilisateur;


import java.util.List;
import java.util.Optional;



public interface UtilisateurRepository
        extends JpaRepository<Utilisateur, Long>, QuerydslPredicateExecutor<Utilisateur> {
    Optional<Utilisateur> findUtilisateurByEmail(String username);


    @Query("SELECT u FROM Utilisateur u JOIN u.profiles p WHERE p.code IN :profileCodes")
    List<Utilisateur> findByProfileCodes(List<String> profileCodes);

    Utilisateur findByEmail(String email);

}
