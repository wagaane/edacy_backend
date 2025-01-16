package sn.ods.starterkit_spring.infrastructure.logging;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    private final ErrorLogService errorLogService;

    public GlobalExceptionHandler(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        // Informations sur l'erreur
        String path = request.getDescription(false); // Chemin de l'API
        String message = ex.getMessage();           // Message d'erreur
        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(); // Code de statut HTTP
        LocalDateTime localDateTime = LocalDateTime.now(); // Date et heure de l'erreur

        // Logger l'erreur dans la console
        logger.error("Error occurred: {}", message, ex);

        // Enregistrer l'erreur dans la base de données
        errorLogService.logError(path, message, statusCode, localDateTime);

        // Retourner une réponse JSON
        ErrorResponse errorResponse = new ErrorResponse(path, message, statusCode, localDateTime);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}