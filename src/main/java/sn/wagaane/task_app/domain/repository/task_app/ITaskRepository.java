package sn.wagaane.task_app.domain.repository.task_app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import sn.wagaane.task_app.domain.model.task_app.Task;

import java.util.Optional;

public interface ITaskRepository extends JpaRepository<Task, Long>, QuerydslPredicateExecutor<Task> {
    Optional<Task> findByIdAndDeletedFalse(long id);
}
