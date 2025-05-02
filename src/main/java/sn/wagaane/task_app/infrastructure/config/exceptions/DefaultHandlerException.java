package sn.wagaane.task_app.infrastructure.config.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sn.wagaane.task_app.presentation.dto.responses.APIMessage;
import sn.wagaane.task_app.presentation.dto.responses.APIResponse;
import sn.wagaane.task_app.presentation.dto.responses.ValidationRspError;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
public class DefaultHandlerException {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleException(ResourceNotFoundException ex, HttpServletRequest request) {

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now());

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);

    }



    //pour les contraintes de validations au niveau des méthodes
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<ValidationRspError> errors = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> ValidationRspError.builder().field(fieldError.getField()).message(fieldError.getDefaultMessage()).build())
                .collect(Collectors.toList());

        // LOGGER.error("{}", errors);

        return ResponseEntity.badRequest().body(APIResponse.error(APIMessage.CONSTRAINT_VIOLATION).errors(errors));
    }

    //pour les contraintes de validations au niveau des objets
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIResponse> handleViolationException(ConstraintViolationException exception) {
        List<ValidationRspError> errors = exception.getConstraintViolations()
                .stream()
                .map(fieldError -> ValidationRspError.builder().field(fieldError.getPropertyPath().toString()).message(fieldError.getMessage()).build())
                .collect(Collectors.toList());

        // LOGGER.error("constraints", errors);
        return ResponseEntity.badRequest().body(APIResponse.error(APIMessage.CONSTRAINT_VIOLATION).errors(errors));

    }



    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiError> handleException(InsufficientAuthenticationException ex, HttpServletRequest request) {

        ApiError apiError = new ApiError(
          request.getRequestURI(),
          ex.getMessage(),
          HttpStatus.FORBIDDEN.value(),
          LocalDateTime.now());
          return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);

  }



    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleException(BadCredentialsException ex, HttpServletRequest request) {

        ApiError apiError = new ApiError(
        request.getRequestURI(),
        ex.getMessage(),
        HttpStatus.UNAUTHORIZED.value(),
        LocalDateTime.now());

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);

    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now());

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
