package sn.ods.starterkit_spring.infrastructure.logging;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogService {


    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Transactional
    public void log(String level, String message) {
        LogEntry logEntry = new LogEntry(level, message);
        logRepository.save(logEntry);
    }
}