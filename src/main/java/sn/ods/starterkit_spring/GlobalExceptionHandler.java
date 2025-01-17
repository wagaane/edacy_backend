package sn.ods.starterkit_spring;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import sn.ods.starterkit_spring.infrastructure.logging.ErrorLogService;
import sn.ods.starterkit_spring.infrastructure.logging.ErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    private final ErrorLogService errorLogService;

    public GlobalExceptionHandler(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
        logger.info("GlobalExceptionHandler initialized with ErrorLogService: {}", errorLogService != null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        logger.info("GlobalExceptionHandler triggered for exception: {}", ex.getMessage());
        // Informations sur l'erreur
        String path = request.getDescription(false); // Chemin de l'API
        String message = ex.getMessage();           // Message d'erreur
        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(); // Code de statut HTTP
        LocalDateTime localDateTime = LocalDateTime.now(); // Date et heure de l'erreur

        try {
            errorLogService.logError(request.getDescription(false), ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Failed to call logError: {}", e.getMessage(), e); // Log en cas d'erreur
        }

        // Retourner une réponse JSON
        ErrorResponse errorResponse = new ErrorResponse(path, message, statusCode, localDateTime);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}