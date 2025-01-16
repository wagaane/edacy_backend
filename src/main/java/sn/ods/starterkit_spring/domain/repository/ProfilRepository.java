package sn.ods.starterkit_spring.domain.repository;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import sn.ods.starterkit_spring.domain.model.utilisateur.Profile;


import java.util.List;
import java.util.Optional;



@Repository
public interface ProfilRepository extends JpaRepository<Profile, Long>, QuerydslPredicateExecutor<Profile> {

    Optional<Profile> findByCode(String code);


}
