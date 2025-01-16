package sn.ods.starterkit_spring.domain.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.ods.starterkit_spring.domain.model.other.FailedMail;



@Repository
public interface FailedMailRepository extends JpaRepository<FailedMail,Long> {
}

