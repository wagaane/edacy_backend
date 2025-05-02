package sn.wagaane.task_app.domain.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.wagaane.task_app.domain.model.other.FailedMail;



@Repository
public interface IFailedMailRepository extends JpaRepository<FailedMail,Long> {
}

