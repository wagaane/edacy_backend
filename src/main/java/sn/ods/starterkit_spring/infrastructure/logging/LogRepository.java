package sn.ods.starterkit_spring.infrastructure.logging;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntry, Long> {
}
