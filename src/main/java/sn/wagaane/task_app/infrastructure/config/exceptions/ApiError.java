package sn.wagaane.task_app.infrastructure.config.exceptions;

import java.time.LocalDateTime;

/**
 * @author Abdou Karim CISSOKHO
 * @created 07/01/2025-11:47
 * @project starterkit-spring
 */
public record ApiError( String path,
                        String message,
                        int statusCode,
                        LocalDateTime localDateTime) {

}
