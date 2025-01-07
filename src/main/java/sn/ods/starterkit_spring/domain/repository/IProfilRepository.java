package sn.ods.starterkit_spring.domain.repository;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import sn.ods.starterkit_spring.domain.model.Profile;


import java.util.List;
import java.util.Optional;



@Repository
public interface IProfilRepository extends JpaRepository<Profile, Long>, QuerydslPredicateExecutor<Profile> {

    Optional<Profile> findByCode(String code);

    List<Profile> findByTypeProfileDivision(String divion);

    List<Profile> findByTypeProfileBureau(String divion);
    List<Profile> findByTypeProfile(String type);

    List<Profile> findByTypeProfileDirection(String divion);
    Profile findProfileByCode(String code);
}
