package sn.ods.starterkit_spring.infrastructure.logging;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ErrorLogService {

    private static final Logger logger = LoggerFactory.getLogger(ErrorLogService.class);

    private final ErrorLogRepository errorLogRepository;

    public ErrorLogService(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    public void logError(String path, String message, int statusCode, LocalDateTime localDateTime) {
        try {
            ErrorLog errorLog = new ErrorLog(path, messge, statusCode, localDateTime);
            errorLogRepository.save(errorLog);
            logger.info("Error logged successfully: {}", message);
        } catch (Exception e) {
            logger.error("Failed to log error: {}", e.getMessage(), e);
        }
    }
}