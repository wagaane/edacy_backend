package sn.ods.starterkit_spring.infrastructure.logging;
import org.springframework.stereotype.Service;

@Service
public class ErrorLogService {


    private final ErrorLogRepository errorLogRepository;

    public ErrorLogService(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    public void logError(String serviceName, String methodName, String errorMessage) {
        ErrorLog errorLog = new ErrorLog(serviceName, methodName, errorMessage);
        errorLogRepository.save(errorLog);
    }
}