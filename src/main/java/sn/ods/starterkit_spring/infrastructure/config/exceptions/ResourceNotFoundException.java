package sn.ods.starterkit_spring.infrastructure.config.exceptions;

/**
 * @author Abdou Karim CISSOKHO
 * @created 07/01/2025-11:48
 * @project starterkit-spring
 */


public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message){
        super(message);
    }

}