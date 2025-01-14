package sn.ods.starterkit_spring.infrastructure.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ErrorLoggingAspect {


    private final ErrorLogService errorLogService;

    public ErrorLoggingAspect(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    // Pointcut pour intercepter toutes les méthodes des services
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    // Après qu'une exception est levée dans une méthode de service
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logError(JoinPoint joinPoint, Exception ex) {
        String serviceName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String errorMessage = ex.getMessage();
        String stackTrace = getStackTraceAsString(ex);

        // Enregistrer l'erreur dans la base de données
        errorLogService.logError(serviceName, methodName, errorMessage);
    }

    // Convertir la stack trace en chaîne de caractères
    private String getStackTraceAsString(Exception ex) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        return stackTrace.toString();
    }
}