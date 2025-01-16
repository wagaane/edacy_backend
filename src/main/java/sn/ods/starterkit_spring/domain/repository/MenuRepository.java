package sn.ods.starterkit_spring.domain.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.ods.starterkit_spring.domain.model.utilisateur.Menu;


/**
 * @author G2k R&D
 */

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
   // ProfileMenuSousMenu findProfileMenuSousMenuByProfileIdAndMenId(Long profileId, Long menuId);
}
