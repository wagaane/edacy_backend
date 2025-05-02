package sn.wagaane.task_app.domain.repository.utilisateur;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.wagaane.task_app.domain.model.other.FailedMail;



@Repository
public interface FailedMailRepository extends JpaRepository<FailedMail,Long> {
}

