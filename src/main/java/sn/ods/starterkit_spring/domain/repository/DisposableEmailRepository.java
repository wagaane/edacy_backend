package sn.ods.starterkit_spring.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.ods.starterkit_spring.domain.model.other.DisposableEmail;


import java.util.Optional;

/**
 * @author G2k R&D
 */

@Repository
public interface DisposableEmailRepository extends JpaRepository<DisposableEmail,Long> {
    Optional<DisposableEmail> findByDomain(String domain);
}
